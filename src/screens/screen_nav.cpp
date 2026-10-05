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
#include "bitmaps/nav_arrows.h"
#include "../device_common.h"

// ===================== Navigation Layout Geometry (368x448) =====================
// Circular distance bar gauge (270° arc centered at y = 178)
#define GAUGE_CX        CX             // 184
#define GAUGE_CY        178            // Matches Figma arc center
#define GAUGE_R_OUTER   163            // Outer radius
#define GAUGE_R_INNER   158            // Inner radius (thickness = 5px, center R = 160.5)
#define GAUGE_START_DEG 135.0f         // Bottom-left opening (135°)
#define GAUGE_END_DEG   45.0f          // Bottom-right opening (45°)
#define GAUGE_SPAN_DEG  270.0f         // Sweep angle across top

// Maneuver icon & dashed stem
#define ICON_CX         CX             // 184
#define ICON_CY         135            // Center of maneuver vector arrow
#define ICON_AREA_X     84
#define ICON_AREA_Y     35
#define ICON_AREA_W     200
#define ICON_AREA_H     265            // Encompasses icon and dashed stem (y = 35 to 300)

// Text regions (opening at bottom)
#define STREET_CY       318            // Street name center Y
#define STREET_RECT_Y   304
#define STREET_RECT_H   30
#define STREET_MAX_W    330

#define DIST_NUM_CY     370            // Distance number center Y (large 24pt bold)
#define DIST_UNIT_CY    422            // Distance unit center Y (12pt bold)
#define DIST_RECT_Y     336
#define DIST_RECT_H     108

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

// ===================== Anti-Aliased Maneuver Bitmap Renderer =====================
static void drawManeuverBitmap(const String &m, uint16_t color) {
  const NavManeuverBitmap *bm = getNavManeuverBitmap(m);
  if (!bm) return;

  // 1. Direct 16-bit RGB565 bitmap (e.g. arrive destination pin with ground ring)
  if (bm->rgb565) {
    gfx->draw16bitRGBBitmap(bm->x, bm->y, (uint16_t *)bm->rgb565, bm->w, bm->h);
    return;
  }

  // 2. Anti-aliased 8-bit Alpha Mask with dynamic tinting
  if (!bm->alpha) return;

  const int16_t w  = bm->w;
  const int16_t h  = bm->h;
  const int16_t x0 = bm->x;
  const int16_t y0 = bm->y;

  // Arrow accent color components
  const uint32_t cr = (color >> 11) & 0x1F;
  const uint32_t cg = (color >> 5) & 0x3F;
  const uint32_t cb = color & 0x1F;

  // Dashed stem fixed 15% white color components (COL_DASH = 0x2965)
  const uint32_t dr = (COL_DASH >> 11) & 0x1F;
  const uint32_t dg = (COL_DASH >> 5) & 0x3F;
  const uint32_t db = COL_DASH & 0x1F;

  // Render in 16-line DMA chunks (w <= 180, so 180 * 16 * 2 = 5,760 bytes)
  #define CHUNK_LINES 16
  static uint16_t s_chunkBuf[180 * CHUNK_LINES];

  for (int16_t y = 0; y < h; y += CHUNK_LINES) {
    int16_t lines = (h - y < CHUNK_LINES) ? (h - y) : CHUNK_LINES;
    for (int16_t row = 0; row < lines; row++) {
      int16_t screenY = y0 + y + row;
      const uint8_t *srcRow = &bm->alpha[(y + row) * w];
      uint16_t *dstRow = &s_chunkBuf[row * w];

      for (int16_t col = 0; col < w; col++) {
        uint8_t a = srcRow[col];
        if (a == 0) {
          dstRow[col] = COL_BG;
        } else if (a <= 45 && screenY >= 205) {
          // Authentic Figma dashed stem (15% white, independent of arrow color)
          uint16_t scale = (uint16_t)a * 255 / 38;
          if (scale > 255) scale = 255;
          uint32_t r = (dr * scale + 128) >> 8;
          uint32_t g = (dg * scale + 128) >> 8;
          uint32_t b = (db * scale + 128) >> 8;
          dstRow[col] = (r << 11) | (g << 5) | b;
        } else {
          // Maneuver arrow body with dynamic color tinting & anti-aliasing
          uint32_t r = (cr * a + 128) >> 8;
          uint32_t g = (cg * a + 128) >> 8;
          uint32_t b = (cb * a + 128) >> 8;
          dstRow[col] = (r << 11) | (g << 5) | b;
        }
      }
    }
    gfx->draw16bitRGBBitmap(x0, y0 + y, s_chunkBuf, w, lines);
  }
}

// Renders the 270° circular Distance Bar (0-100% distance to maneuver)
static void drawDistanceBar() {
  // Completely remove distance arc bars on arrival or ended screens
  if (g_maneuver == "arrive" || g_distance == "Arrived" || g_navEnded) {
    return;
  }

  uint16_t trackColor = blend565(g_colNavBar, COL_BG, 51);

  // 1. Draw inactive background track (full 270° arc from 135° to 45° across top)
  gfx->fillArc(GAUGE_CX, GAUGE_CY, GAUGE_R_OUTER, GAUGE_R_INNER,
               GAUGE_START_DEG, GAUGE_END_DEG, trackColor);

  // 2. Draw active progress bar
  float p = constrain(g_navProgress, 0.0f, 1.0f);
  if (p > 0.005f) {
    if (p >= 0.995f) {
      gfx->fillArc(GAUGE_CX, GAUGE_CY, GAUGE_R_OUTER, GAUGE_R_INNER,
                   GAUGE_START_DEG, GAUGE_END_DEG, g_colNavBar);
    } else {
      float span = p * GAUGE_SPAN_DEG;
      if (g_barDir == 1) {
        // Right > Left: fills from 45° (bottom-right) counter-clockwise across top
        float startDeg = fmodf(GAUGE_END_DEG - span + 360.0f, 360.0f);
        gfx->fillArc(GAUGE_CX, GAUGE_CY, GAUGE_R_OUTER, GAUGE_R_INNER,
                     startDeg, GAUGE_END_DEG, g_colNavBar);

        // Rounded tip cap on active moving end
        float rad = startDeg * DEG_TO_RAD;
        int16_t capX = GAUGE_CX + (int16_t)roundf(160.5f * cosf(rad));
        int16_t capY = GAUGE_CY + (int16_t)roundf(160.5f * sinf(rad));
        gfx->fillCircle(capX, capY, 2, g_colNavBar);

        // Rounded cap on fixed 45° right end
        float eRad = GAUGE_END_DEG * DEG_TO_RAD;
        int16_t eX = GAUGE_CX + (int16_t)roundf(160.5f * cosf(eRad));
        int16_t eY = GAUGE_CY + (int16_t)roundf(160.5f * sinf(eRad));
        gfx->fillCircle(eX, eY, 2, g_colNavBar);
      } else {
        // Left > Right (Default): fills from 135° (bottom-left) clockwise across top
        float endDeg = fmodf(GAUGE_START_DEG + span, 360.0f);
        gfx->fillArc(GAUGE_CX, GAUGE_CY, GAUGE_R_OUTER, GAUGE_R_INNER,
                     GAUGE_START_DEG, endDeg, g_colNavBar);

        // Rounded tip cap on active moving end
        float rad = endDeg * DEG_TO_RAD;
        int16_t capX = GAUGE_CX + (int16_t)roundf(160.5f * cosf(rad));
        int16_t capY = GAUGE_CY + (int16_t)roundf(160.5f * sinf(rad));
        gfx->fillCircle(capX, capY, 2, g_colNavBar);

        // Rounded cap on fixed 135° left end
        float sRad = GAUGE_START_DEG * DEG_TO_RAD;
        int16_t sX = GAUGE_CX + (int16_t)roundf(160.5f * cosf(sRad));
        int16_t sY = GAUGE_CY + (int16_t)roundf(160.5f * sinf(sRad));
        gfx->fillCircle(sX, sY, 2, g_colNavBar);
      }
    }
  }
}

// ===================== Sub-region Renderers =====================
static void drawIconRegion() {
  if (g_maneuver == "unknown" && g_iconValid && g_iconCanvas) {
    const int16_t x0 = ICON_CX - OUT_ICON_W / 2, y0 = ICON_CY - OUT_ICON_H / 2;
    gfx->fillRect(ICON_AREA_X, ICON_AREA_Y, ICON_AREA_W, ICON_AREA_H, COL_BG);
    scale3xIcon();
    renderIconCanvas();
    gfx->draw16bitRGBBitmap(x0, y0, g_iconCanvas, OUT_ICON_W, OUT_ICON_H);
    return;
  }

  gfx->fillRect(ICON_AREA_X, ICON_AREA_Y, ICON_AREA_W, ICON_AREA_H, COL_BG);
  drawManeuverBitmap(g_maneuver, g_colNavArrow);
}

static void drawDistRegion() {
  gfx->fillRect(10, DIST_RECT_Y, SCREEN_W - 20, DIST_RECT_H, COL_BG);
  if (!g_distance.length()) return;

  if (g_maneuver == "arrive" || g_distance == "Arrived") {
    if (g_distance == "0 m") {
      drawTextC("0 m", DIST_NUM_CY, &SFCompactBold24pt7b, 0x07E8 /* green */);
      drawTextC((g_street.length() > 0) ? fitStreet(g_street, &SFCompactBold12pt7b, STREET_MAX_W) : "Destination ahead",
                DIST_UNIT_CY, &SFCompactBold12pt7b, 0xFFFF /* white */);
    } else {
      drawTextC("Arrived", DIST_NUM_CY, &SFCompactBold24pt7b, 0x07E8 /* green */);
      drawTextC((g_street.length() > 0) ? fitStreet(g_street, &SFCompactBold12pt7b, STREET_MAX_W) : "Destination",
                DIST_UNIT_CY, &SFCompactBold12pt7b, 0xFFFF /* white */);
    }
    return;
  }

  // Split into distance number and distance unit (e.g. "50 m" -> "50", "m")
  String num, unit;
  int spaceIdx = g_distance.lastIndexOf(' ');
  if (spaceIdx > 0) {
    num  = g_distance.substring(0, spaceIdx);
    unit = g_distance.substring(spaceIdx + 1);
  } else {
    num  = g_distance;
    unit = "";
  }

  // Draw giant distance number in SF Compact Bold 48pt (or 24pt if > 4 chars)
  if (num.length() <= 4) {
    drawTextC(num, DIST_NUM_CY, &SFCompactBold48pt7b, g_colNavDist);
  } else {
    drawTextC(num, DIST_NUM_CY, &SFCompactBold24pt7b, g_colNavDist);
  }

  // Draw unit centered below number at DIST_UNIT_CY in SF Compact Bold 12pt
  if (unit.length() > 0) {
    drawTextC(unit, DIST_UNIT_CY, &SFCompactBold12pt7b, COL_DIM_WHITE);
  }
}

static void drawStreetRegion() {
  gfx->fillRect(10, STREET_RECT_Y, SCREEN_W - 20, STREET_RECT_H, COL_BG);
  if (g_street.length() && g_maneuver != "arrive" && g_distance != "Arrived") {
    drawTextC(fitStreet(g_street, &SFCompactBold12pt7b, STREET_MAX_W),
              STREET_CY, &SFCompactBold12pt7b, COL_DIM_WHITE);
  }
}

// ===================== Waiting & Ended Screens =====================
static void drawWaiting() {
  gfx->fillScreen(COL_BG);
  if (g_connected) {
    drawTextC("CONNECTED", CY - 16, &SFCompactBold12pt7b, 0xFFFF);
    drawTextC("WAITING",   CY + 20, &SFCompactBold12pt7b, 0x8410);
  } else {
    drawTextC("NO PHONE",  CY - 16, &SFCompactBold12pt7b, 0xFFFF);
    drawTextC("WAITING",   CY + 20, &SFCompactBold12pt7b, 0x8410);
  }
}

static void drawEnded() {
  gfx->fillScreen(COL_BG);
  drawManeuverBitmap("arrive", 0x07E8);
  drawTextC("Arrived", DIST_NUM_CY, &SFCompactBold24pt7b, 0x07E8);
  drawTextC("Navigation ended", DIST_UNIT_CY, &SFCompactBold12pt7b, 0xFFFF);
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
    drawDistanceBar();
    drawDistRegion();
    drawStreetRegion();
    g_navShown = true;
  } else {
    if (g_dirtyIcon) {
      drawIconRegion();
      drawDistanceBar();
    }
    if (g_dirtyDist) {
      if (!g_dirtyIcon) drawDistanceBar();
      drawDistRegion();
    }
    if (g_dirtyStreet) drawStreetRegion();
  }
}

void screen_nav_animate_waiting() {
  // Spinner removed for power savings — static waiting screen uses 0 QSPI transfers
}
