/*
 * screen_nav.cpp — Navigation & Waiting Screen Component (SCR_MAIN)
 *
 * Handles:
 *   - Idle waiting screen & circular progress spinner
 *   - Active turn-by-turn navigation layout (200x200 vector arrows, distance, street)
 *   - Scale3x + bilinear iso-contour renderer for unknown phone bitmap icons
 *   - Navigation ended confirmation screen
 */

#include <math.h>
#include "screen_nav.h"
#include "../device_common.h"

// ===================== Navigation Layout Geometry (368x448) =====================
#define ICON_CX         CX
#define ICON_CY         135
#define ICON_AREA_X     84
#define ICON_AREA_Y     35
#define ICON_AREA_W     200
#define ICON_AREA_H     200

#define DIST_CY         282
#define DIST_RECT_Y     246
#define DIST_RECT_H     70

#define STREET_CY       358
#define STREET_RECT_Y   330
#define STREET_RECT_H   52
#define STREET_MAX_W    310

// ===================== Waiting Screen Spinner Constants =====================
#define SPIN_R          150     // Spinner radius for 368x448 display
#define SPIN_LEN        90      // Arc length in degrees
#define SPIN_STEP       2       // Degrees advanced per frame

static int      s_spinDeg   = 0;
static uint32_t s_lastFrame = 0;

// ===================== Scale3x + Bilinear Icon Upscaler (Fallback) =====================
#define BIG_W           (ICON_W * 3)   // 120
#define BIG_H           (ICON_H * 3)   // 120

static uint8_t s_iconBig[BIG_W * BIG_H / 8];

static inline bool srcPx(int x, int y) {
  if (x < 0) x = 0; else if (x >= ICON_W) x = ICON_W - 1;
  if (y < 0) y = 0; else if (y >= ICON_H) y = ICON_H - 1;
  int bit = y * ICON_W + x;
  return (g_iconBits[bit >> 3] & (0x80 >> (bit & 7))) != 0;
}

static inline void bigSet(int x, int y) {
  int bit = y * BIG_W + x;
  s_iconBig[bit >> 3] |= (0x80 >> (bit & 7));
}

static inline bool bigPx(int x, int y) {
  if (x < 0) x = 0; else if (x >= BIG_W) x = BIG_W - 1;
  if (y < 0) y = 0; else if (y >= BIG_H) y = BIG_H - 1;
  int bit = y * BIG_W + x;
  return (s_iconBig[bit >> 3] & (0x80 >> (bit & 7))) != 0;
}

static void scale3xIcon() {
  memset(s_iconBig, 0, sizeof(s_iconBig));
  for (int y = 0; y < ICON_H; y++) {
    for (int x = 0; x < ICON_W; x++) {
      bool E = srcPx(x, y);
      bool A = srcPx(x - 1, y - 1), B = srcPx(x, y - 1), C = srcPx(x + 1, y - 1);
      bool D = srcPx(x - 1, y),                          F = srcPx(x + 1, y);
      bool G = srcPx(x - 1, y + 1), H = srcPx(x, y + 1), I = srcPx(x + 1, y + 1);

      bool e0 = E, e1 = E, e2 = E, e3 = E, e5 = E, e6 = E, e7 = E, e8 = E;
      if (B != H && D != F) {
        e0 = (D == B) ? D : E;
        e1 = ((D == B && E != C) || (B == F && E != A)) ? B : E;
        e2 = (B == F) ? F : E;
        e3 = ((D == B && E != G) || (D == H && E != A)) ? D : E;
        e5 = ((B == F && E != I) || (H == F && E != C)) ? F : E;
        e6 = (D == H) ? D : E;
        e7 = ((D == H && E != I) || (H == F && E != G)) ? H : E;
        e8 = (H == F) ? F : E;
      }
      int bx = x * 3, by = y * 3;
      if (e0) bigSet(bx, by);     if (e1) bigSet(bx + 1, by);     if (e2) bigSet(bx + 2, by);
      if (e3) bigSet(bx, by + 1); if (E)  bigSet(bx + 1, by + 1); if (e5) bigSet(bx + 2, by + 1);
      if (e6) bigSet(bx, by + 2); if (e7) bigSet(bx + 1, by + 2); if (e8) bigSet(bx + 2, by + 2);
    }
  }
}

static inline int bigDensity(int sx, int sy) {
  int sum = 0;
  sum += bigPx(sx,     sy)     ? 64 : 0;
  sum += bigPx(sx - 1, sy)     ? 32 : 0;
  sum += bigPx(sx + 1, sy)     ? 32 : 0;
  sum += bigPx(sx,     sy - 1) ? 32 : 0;
  sum += bigPx(sx,     sy + 1) ? 32 : 0;
  sum += bigPx(sx - 1, sy - 1) ? 16 : 0;
  sum += bigPx(sx + 1, sy - 1) ? 16 : 0;
  sum += bigPx(sx - 1, sy + 1) ? 16 : 0;
  sum += bigPx(sx + 1, sy + 1) ? 16 : 0;
  return sum;
}

static void renderIconCanvas() {
  if (!g_iconCanvas) return;
  for (int y = 0; y < OUT_ICON_H; y++) {
    int fy = (y * (BIG_H - 1) * 256) / (OUT_ICON_H - 1);
    int sy = fy >> 8;
    int wy = fy & 0xFF;
    int sy1 = (sy + 1 < BIG_H) ? sy + 1 : sy;

    for (int x = 0; x < OUT_ICON_W; x++) {
      int fx = (x * (BIG_W - 1) * 256) / (OUT_ICON_W - 1);
      int sx = fx >> 8;
      int wx = fx & 0xFF;
      int sx1 = (sx + 1 < BIG_W) ? sx + 1 : sx;

      int d00 = bigDensity(sx,  sy);
      int d10 = bigDensity(sx1, sy);
      int d01 = bigDensity(sx,  sy1);
      int d11 = bigDensity(sx1, sy1);

      int top = (d00 * (256 - wx) + d10 * wx) >> 8;
      int bot = (d01 * (256 - wx) + d11 * wx) >> 8;
      int val = (top * (256 - wy) + bot * wy) >> 8;

      uint16_t c;
      if (val >= 146) {
        c = g_colNavArrow;
      } else if (val <= 110) {
        c = COL_BG;
      } else {
        uint8_t alpha = (uint8_t)(((val - 110) * 255) / 36);
        c = blend565(g_colNavArrow, COL_BG, alpha);
      }
      g_iconCanvas[y * OUT_ICON_W + x] = c;
    }
  }
}

// ===================== Vector Maneuver Arrow Primitives =====================
static void rotatePt(int16_t cx0, int16_t cy0, float x, float y, float rad,
                     int16_t &ox, int16_t &oy) {
  float c = cosf(rad), s = sinf(rad);
  ox = (int16_t)lroundf(cx0 + (x * c - y * s));
  oy = (int16_t)lroundf(cy0 + (x * s + y * c));
}

// Draws a thick rounded-cap road segment from (x0, y0) to (x1, y1)
static void fillThickSeg(int16_t x0, int16_t y0, int16_t x1, int16_t y1,
                         int16_t r, uint16_t color) {
  gfx->fillCircle(x0, y0, r, color);
  gfx->fillCircle(x1, y1, r, color);
  float dx = (float)(x1 - x0), dy = (float)(y1 - y0);
  float len = sqrtf(dx * dx + dy * dy);
  if (len < 1.0f) return;
  float nx = -dy / len * r, ny = dx / len * r;
  int16_t ax = (int16_t)lroundf(x0 + nx), ay = (int16_t)lroundf(y0 + ny);
  int16_t bx = (int16_t)lroundf(x0 - nx), by = (int16_t)lroundf(y0 - ny);
  int16_t cx = (int16_t)lroundf(x1 - nx), cy = (int16_t)lroundf(y1 - ny);
  int16_t dx_ = (int16_t)lroundf(x1 + nx), dy_ = (int16_t)lroundf(y1 + ny);
  gfx->fillTriangle(ax, ay, bx, by, cx, cy, color);
  gfx->fillTriangle(ax, ay, cx, cy, dx_, dy_, color);
}

// Draws a bold triangular arrowhead with its tip at (tipX, tipY)
static void drawArrowHead(int16_t tipX, int16_t tipY, float angleDeg,
                          float size, uint16_t color) {
  float rad = angleDeg * PI / 180.0f;
  int16_t ax, ay, bx, by, cx, cy;
  rotatePt(tipX, tipY, 0,             0,    rad, ax, ay);
  rotatePt(tipX, tipY, -size * 0.85f, size, rad, bx, by);
  rotatePt(tipX, tipY,  size * 0.85f, size, rad, cx, cy);
  gfx->fillTriangle(ax, ay, bx, by, cx, cy, color);
}

static void drawArrive(int16_t cx0, int16_t cy0, uint16_t color) {
  // Ground target ring beneath pin tip
  gfx->fillCircle(cx0, cy0 + 68, 26, 0x1B45);
  gfx->fillCircle(cx0, cy0 + 68, 13, COL_BG);
  // Map pin head + tapered teardrop point
  gfx->fillCircle(cx0, cy0 - 20, 46, color);
  gfx->fillTriangle(cx0 - 42, cy0 - 2, cx0 + 42, cy0 - 2, cx0, cy0 + 66, color);
  // Inner pin cutout
  gfx->fillCircle(cx0, cy0 - 20, 18, COL_BG);
}

static void drawRoundabout(int16_t cx0, int16_t cy0, uint16_t color) {
  fillThickSeg(cx0 - 12, cy0 + 82, cx0 - 12, cy0 + 42, 12, color);
  gfx->fillCircle(cx0 - 12, cy0 + 8, 44, color);
  gfx->fillCircle(cx0 - 12, cy0 + 8, 22, COL_BG);
  fillThickSeg(cx0 + 16, cy0 - 16, cx0 + 54, cy0 - 50, 12, color);
  drawArrowHead(cx0 + 82, cy0 - 74, 48.0f, 44.0f, color);
}

// Renders crisp 200x200 Google Maps-style bent maneuver arrows
static void drawManeuverVector(int16_t cx0, int16_t cy0, const String &m, uint16_t color) {
  const int16_t R = 14;   // 28px road stem thickness
  const float   H = 52.0f; // Arrowhead size

  if (m == "roundabout") {
    drawRoundabout(cx0, cy0, color);
  } else if (m == "arrive") {
    drawArrive(cx0, cy0, color);
  } else if (m == "turn-right" || m == "right") {
    fillThickSeg(cx0 - 28, cy0 + 80, cx0 - 28, cy0 - 18, R, color);
    fillThickSeg(cx0 - 28, cy0 - 18, cx0 + 38, cy0 - 18, R, color);
    drawArrowHead(cx0 + 84, cy0 - 18, 90.0f, H, color);
  } else if (m == "turn-left" || m == "left") {
    fillThickSeg(cx0 + 28, cy0 + 80, cx0 + 28, cy0 - 18, R, color);
    fillThickSeg(cx0 + 28, cy0 - 18, cx0 - 38, cy0 - 18, R, color);
    drawArrowHead(cx0 - 84, cy0 - 18, -90.0f, H, color);
  } else if (m == "slight-right" || m == "keep-right" || m == "merge") {
    fillThickSeg(cx0 - 22, cy0 + 80, cx0 - 22, cy0 + 6, R, color);
    fillThickSeg(cx0 - 22, cy0 + 6,  cx0 + 32, cy0 - 48, R, color);
    drawArrowHead(cx0 + 66, cy0 - 82, 45.0f, H, color);
  } else if (m == "slight-left" || m == "keep-left") {
    fillThickSeg(cx0 + 22, cy0 + 80, cx0 + 22, cy0 + 6, R, color);
    fillThickSeg(cx0 + 22, cy0 + 6,  cx0 - 32, cy0 - 48, R, color);
    drawArrowHead(cx0 - 66, cy0 - 82, -45.0f, H, color);
  } else if (m == "sharp-right") {
    fillThickSeg(cx0 - 30, cy0 + 76, cx0 - 30, cy0 - 46, R, color);
    fillThickSeg(cx0 - 30, cy0 - 46, cx0 + 28, cy0 + 12, R, color);
    drawArrowHead(cx0 + 62, cy0 + 46, 135.0f, H, color);
  } else if (m == "sharp-left") {
    fillThickSeg(cx0 + 30, cy0 + 76, cx0 + 30, cy0 - 46, R, color);
    fillThickSeg(cx0 + 30, cy0 - 46, cx0 - 28, cy0 + 12, R, color);
    drawArrowHead(cx0 - 62, cy0 + 46, -135.0f, H, color);
  } else if (m == "uturn" || m == "u-turn") {
    fillThickSeg(cx0 + 36, cy0 + 80, cx0 + 36, cy0 - 28, 13, color);
    fillThickSeg(cx0 + 36, cy0 - 28, cx0 + 14, cy0 - 56, 13, color);
    fillThickSeg(cx0 + 14, cy0 - 56, cx0 - 14, cy0 - 56, 13, color);
    fillThickSeg(cx0 - 14, cy0 - 56, cx0 - 36, cy0 - 28, 13, color);
    fillThickSeg(cx0 - 36, cy0 - 28, cx0 - 36, cy0 + 20, 13, color);
    drawArrowHead(cx0 - 36, cy0 + 68, 180.0f, 48.0f, color);
  } else {
    fillThickSeg(cx0, cy0 + 80, cx0, cy0 - 36, R, color);
    drawArrowHead(cx0, cy0 - 86, 0.0f, H, color);
  }
}

// ===================== Sub-region Renderers =====================
static void drawIconRegion() {
  if (g_maneuver == "unknown" && g_iconValid && g_iconCanvas) {
    const int16_t x0 = ICON_CX - OUT_ICON_W / 2, y0 = ICON_CY - OUT_ICON_H / 2;
    gfx->fillRect(ICON_AREA_X, ICON_AREA_Y, x0 - ICON_AREA_X, ICON_AREA_H, COL_BG);
    gfx->fillRect(x0 + OUT_ICON_W, ICON_AREA_Y,
                  ICON_AREA_X + ICON_AREA_W - (x0 + OUT_ICON_W), ICON_AREA_H, COL_BG);
    gfx->fillRect(x0, ICON_AREA_Y, OUT_ICON_W, y0 - ICON_AREA_Y, COL_BG);
    gfx->fillRect(x0, y0 + OUT_ICON_H, OUT_ICON_W,
                  ICON_AREA_Y + ICON_AREA_H - (y0 + OUT_ICON_H), COL_BG);
    scale3xIcon();
    renderIconCanvas();
    gfx->draw16bitRGBBitmap(x0, y0, g_iconCanvas, OUT_ICON_W, OUT_ICON_H);
    return;
  }

  gfx->fillRect(ICON_AREA_X, ICON_AREA_Y, ICON_AREA_W, ICON_AREA_H, COL_BG);
  drawManeuverVector(ICON_CX, ICON_CY, g_maneuver, g_colNavArrow);
}

static void drawDistRegion() {
  gfx->fillRect(20, DIST_RECT_Y, SCREEN_W - 40, DIST_RECT_H, COL_BG);
  if (g_distance.length()) {
    drawTextC(g_distance, DIST_CY, &FreeSansBold24pt7b, g_colNavDist);
  }
}

static void drawStreetRegion() {
  gfx->fillRect(20, STREET_RECT_Y, SCREEN_W - 40, STREET_RECT_H, COL_BG);
  if (g_street.length())
    drawTextC(fitStreet(g_street, &FreeSansBold12pt7b, STREET_MAX_W),
              STREET_CY, &FreeSansBold12pt7b, 0xFFFF);
}

// ===================== Waiting & Ended Screens =====================
static void drawWaiting() {
  gfx->fillScreen(COL_BG);
  if (g_connected) {
    drawTextC("CONNECTED", CY - 16, &FreeSansBold12pt7b, 0xFFFF);
    drawTextC("WAITING",   CY + 20, &FreeSansBold12pt7b, 0x8410);
  } else {
    drawTextC("NO PHONE",  CY - 16, &FreeSansBold12pt7b, 0xFFFF);
    drawTextC("WAITING",   CY + 20, &FreeSansBold12pt7b, 0x8410);
  }
}

static void drawEnded() {
  gfx->fillScreen(COL_BG);
  drawArrive(ICON_CX, ICON_CY, g_colNavArrow);
  drawTextC("Arrived", DIST_CY, &FreeSansBold24pt7b, g_colNavDist);
  drawTextC("Navigation ended", STREET_CY, &FreeSansBold12pt7b, 0xFFFF);
}

// ===================== Public Component Entry Points =====================
void screen_nav_draw() {
  bool hasNav = g_maneuver.length() > 0 &&
                g_maneuver != "clear" && g_maneuver != "end";

  if (!hasNav) {
    if (g_navEnded) drawEnded();
    else            drawWaiting();
    g_navShown = false;
  } else if (g_dirtyAll || !g_navShown) {
    gfx->fillScreen(COL_BG);
    drawIconRegion();
    drawDistRegion();
    drawStreetRegion();
    g_navShown = true;
  } else {
    if (g_dirtyIcon)   drawIconRegion();
    if (g_dirtyDist)   drawDistRegion();
    if (g_dirtyStreet) drawStreetRegion();
  }
}

void screen_nav_animate_waiting() {
  // Spinner removed for power savings — static waiting screen uses 0 QSPI transfers
}
