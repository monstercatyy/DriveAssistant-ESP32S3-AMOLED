/*
 * screen_map.cpp - Beeline Moto 2 Style 3D 40-Degree Perspective Mini Map (SCR_MAP)
 *
 * Beeline Moto 2 Style 3D Navigation Display:
 *   - 40-degree downward 3D perspective projection looking ahead from vehicle
 *   - Pure black background (COL_BG = 0x0000) for high contrast and AMOLED power efficiency
 *   - Solid pure white active route line (COL_ROUTE = 0xFFFF)
 *   - Solid muted gray non-route streets, crossroads, and alternate branches (COL_STREET = 0x632C)
 *   - Anchored 3D vehicle chevron at bottom-center pointing straight ahead
 *   - Minimal Beeline-style floating HUD cards for turn distance, maneuver icon, and street
 */

#include "screen_map.h"
#include "../device_common.h"
#include <math.h>

// ===================== 3D Geometry Constants (368x448) =====================
#define Y_HORIZON          76    // Vanishing horizon Y coordinate (below top HUD)
#define Y_BOTTOM_HUD       386   // Top of bottom HUD card deck
#define Y_CAR              354   // Vehicle anchor Y coordinate (above bottom HUD)
#define ROAD_W_ACTIVE      36    // Base width of active route line at vehicle (large solid ribbon)
#define ROAD_W_SIDE        28    // Base width of non-route double road borders (thicker curbs)
#define COL_MAP_ROUTE      0xFFFF // Solid white active route
#define COL_MAP_ROUTE_AA   0x7BEF // Soft antialiased fringe (~45% white: #808080)
#define COL_MAP_GRAY       0xAD75 // Crisp bright silver for non-route road borders (~#ADADAD)
#define COL_MAP_GRAY_AA    0x4208 // Soft antialiased fringe (~40% gray: #404040)
#define COL_CARD_LINE      0x2965 // Dim divider line (15% white)

// ===================== 3D Clipping & Perspective Projection =====================
static constexpr float Z_NEAR_CLIP = 0.0f;
static constexpr float Z_FAR_CLIP  = 260.0f;

// Clips a 3D ground segment against [Z_NEAR_CLIP, Z_FAR_CLIP].
// Returns false if the segment is completely outside the visible depth range.
static inline bool clipSegment3D(float &x0, float &z0, float &x1, float &z1) {
  if (z0 <= Z_NEAR_CLIP && z1 <= Z_NEAR_CLIP) return false;
  if (z0 >= Z_FAR_CLIP  && z1 >= Z_FAR_CLIP)  return false;

  // Clip against near plane (z = 0.0f)
  if (z0 < Z_NEAR_CLIP) {
    float dz = z1 - z0;
    if (fabsf(dz) < 0.001f) return false;
    float t = (Z_NEAR_CLIP - z0) / dz;
    x0 = x0 + t * (x1 - x0);
    z0 = Z_NEAR_CLIP;
  } else if (z1 < Z_NEAR_CLIP) {
    float dz = z0 - z1;
    if (fabsf(dz) < 0.001f) return false;
    float t = (Z_NEAR_CLIP - z1) / dz;
    x1 = x1 + t * (x0 - x1);
    z1 = Z_NEAR_CLIP;
  }

  // Clip against far horizon plane (z = 260.0f)
  if (z0 > Z_FAR_CLIP) {
    float dz = z0 - z1;
    if (fabsf(dz) < 0.001f) return false;
    float t = (Z_FAR_CLIP - z1) / dz;
    x0 = x1 + t * (x0 - x1);
    z0 = Z_FAR_CLIP;
  } else if (z1 > Z_FAR_CLIP) {
    float dz = z1 - z0;
    if (fabsf(dz) < 0.001f) return false;
    float t = (Z_FAR_CLIP - z0) / dz;
    x1 = x0 + t * (x1 - x0);
    z1 = Z_FAR_CLIP;
  }

  return true;
}

// Transforms ground coordinates (x_m: lateral meters, z_m: forward meters ahead)
// into 2D screen coordinates (sx, sy) and perspective half-width (hw).
static inline void project3D(float x_m, float z_m, int16_t &sx, int16_t &sy, float &hw, float baseW) {
  if (z_m < Z_NEAR_CLIP) z_m = Z_NEAR_CLIP;
  if (z_m > Z_FAR_CLIP)  z_m = Z_FAR_CLIP;

  // 40-degree downward pitch projection foreshortening
  float denom  = 1.0f + 0.009f * z_m;
  float y_span = (float)(Y_CAR - Y_HORIZON);
  float y_proj = (float)Y_HORIZON + (y_span / denom);

  // Perspective scale: 1.0 at vehicle anchor, approaching 0.0 at horizon
  float scale = (y_proj - (float)Y_HORIZON) / y_span;
  scale = constrain(scale, 0.0f, 1.0f);

  // Clamp lateral screen projection to prevent int16_t overflow on wide cross streets
  float x_proj = constrain((float)CX + (x_m * scale * 2.0f), -512.0f, 880.0f);

  sx = (int16_t)roundf(x_proj);
  sy = (int16_t)roundf(y_proj);
  // Guarantee minimum 2.5px half-width (5px total width) even at horizon
  hw = max(2.5f, (baseW * scale) * 0.5f);
}

// Low-level solid perspective ribbon primitive with rounded joint caps
static void draw3DRibbonRaw(float x0, float z0, float x1, float z1, float baseW, uint16_t color, float extraHalfPx = 0.0f) {
  if (!clipSegment3D(x0, z0, x1, z1)) return;

  int16_t sx0, sy0, sx1, sy1;
  float hw0, hw1;
  project3D(x0, z0, sx0, sy0, hw0, baseW);
  project3D(x1, z1, sx1, sy1, hw1, baseW);

  hw0 = max(1.5f, hw0 + extraHalfPx);
  hw1 = max(1.5f, hw1 + extraHalfPx);

  float dx = (float)(sx1 - sx0);
  float dy = (float)(sy1 - sy0);
  float len = sqrtf(dx * dx + dy * dy);

  int16_t r0 = (int16_t)roundf(hw0);
  int16_t r1 = (int16_t)roundf(hw1);

  if (len < 1.0f) {
    gfx->fillCircle(sx0, sy0, max((int16_t)1, r0), color);
    return;
  }

  float nx = -dy / len;
  float ny =  dx / len;

  int16_t p1x = (int16_t)roundf((float)sx0 + nx * hw0);
  int16_t p1y = (int16_t)roundf((float)sy0 + ny * hw0);
  int16_t p2x = (int16_t)roundf((float)sx0 - nx * hw0);
  int16_t p2y = (int16_t)roundf((float)sy0 - ny * hw0);
  int16_t p3x = (int16_t)roundf((float)sx1 - nx * hw1);
  int16_t p3y = (int16_t)roundf((float)sy1 - ny * hw1);
  int16_t p4x = (int16_t)roundf((float)sx1 + nx * hw1);
  int16_t p4y = (int16_t)roundf((float)sy1 + ny * hw1);

  gfx->fillTriangle(p1x, p1y, p2x, p2y, p3x, p3y, color);
  gfx->fillTriangle(p1x, p1y, p3x, p3y, p4x, p4y, color);
  gfx->fillCircle(sx0, sy0, r0, color);
  gfx->fillCircle(sx1, sy1, r1, color);
}

// Draws a solid perspective route segment with outer AA halo
static void draw3DSegment(float x0, float z0, float x1, float z1, int16_t baseW, uint16_t color) {
  if (color == COL_MAP_ROUTE) {
    draw3DRibbonRaw(x0, z0, x1, z1, (float)baseW, COL_MAP_ROUTE_AA, 1.0f);
  }
  draw3DRibbonRaw(x0, z0, x1, z1, (float)baseW, color, 0.0f);
}

// Draws a non-route road as double border lines that taper and fade into darkness like a strand of hair
static void draw3DBorderRoad(float x0, float z0, float x1, float z1, int16_t baseW, uint16_t color) {
  if (!clipSegment3D(x0, z0, x1, z1)) return;

  // Ensure (x0, z0) is the root (near route / intersection) and (x1, z1) is the outward tail
  float dist0 = x0 * x0 + (z0 * 0.15f) * (z0 * 0.15f);
  float dist1 = x1 * x1 + (z1 * 0.15f) * (z1 * 0.15f);
  if (dist0 > dist1) {
    float tx = x0; x0 = x1; x1 = tx;
    float tz = z0; z0 = z1; z1 = tz;
  }

  // 7 smooth gradient steps tapering thickness and fading alpha to 0 (hair strand fade)
  static const float   tSteps[8]    = { 0.0f,  0.28f, 0.48f, 0.65f, 0.78f, 0.88f, 0.95f, 1.0f };
  static const uint8_t alphas[7]    = { 255,   240,   185,   125,   65,    25,    8 };
  static const float   curbHalfW[8] = { 1.8f,  1.7f,  1.4f,  1.1f,  0.8f,  0.5f,  0.3f, 0.1f };

  for (int step = 0; step < 7; step++) {
    float ta = tSteps[step];
    float tb = tSteps[step + 1];

    float xa = x0 + ta * (x1 - x0);
    float za = z0 + ta * (z1 - z0);
    float xb = x0 + tb * (x1 - x0);
    float zb = z0 + tb * (z1 - z0);

    int16_t sxa, sya, sxb, syb;
    float hwa, hwb;
    project3D(xa, za, sxa, sya, hwa, (float)baseW);
    project3D(xb, zb, sxb, syb, hwb, (float)baseW);

    float dx = (float)(sxb - sxa);
    float dy = (float)(syb - sya);
    float len = sqrtf(dx * dx + dy * dy);
    if (len < 0.5f) continue;

    float nx = -dy / len;
    float ny =  dx / len;

    float chwa = curbHalfW[step];
    float chwb = curbHalfW[step + 1];

    uint16_t stepColor = blend565(color, COL_BG, alphas[step]);

    // 1. Right curb ribbon
    int16_t r1x = (int16_t)roundf((float)sxa + nx * (hwa + chwa));
    int16_t r1y = (int16_t)roundf((float)sya + ny * (hwa + chwa));
    int16_t r2x = (int16_t)roundf((float)sxa + nx * (hwa - chwa));
    int16_t r2y = (int16_t)roundf((float)sya + ny * (hwa - chwa));
    int16_t r3x = (int16_t)roundf((float)sxb + nx * (hwb - chwb));
    int16_t r3y = (int16_t)roundf((float)syb + ny * (hwb - chwb));
    int16_t r4x = (int16_t)roundf((float)sxb + nx * (hwb + chwb));
    int16_t r4y = (int16_t)roundf((float)syb + ny * (hwb + chwb));

    gfx->fillTriangle(r1x, r1y, r2x, r2y, r3x, r3y, stepColor);
    gfx->fillTriangle(r1x, r1y, r3x, r3y, r4x, r4y, stepColor);

    // 2. Left curb ribbon
    int16_t l1x = (int16_t)roundf((float)sxa - nx * (hwa - chwa));
    int16_t l1y = (int16_t)roundf((float)sya - ny * (hwa - chwa));
    int16_t l2x = (int16_t)roundf((float)sxa - nx * (hwa + chwa));
    int16_t l2y = (int16_t)roundf((float)sya - ny * (hwa + chwa));
    int16_t l3x = (int16_t)roundf((float)sxb - nx * (hwb + chwb));
    int16_t l3y = (int16_t)roundf((float)syb - ny * (hwb + chwb));
    int16_t l4x = (int16_t)roundf((float)sxb - nx * (hwb - chwb));
    int16_t l4y = (int16_t)roundf((float)syb - ny * (hwb - chwb));

    gfx->fillTriangle(l1x, l1y, l2x, l2y, l3x, l3y, stepColor);
    gfx->fillTriangle(l1x, l1y, l3x, l3y, l4x, l4y, stepColor);

    // Step joint caps (curbs only, never bridging the road)
    if (alphas[step] > 70) {
      int16_t cr0x = (int16_t)roundf((float)sxa + nx * hwa);
      int16_t cr0y = (int16_t)roundf((float)sya + ny * hwa);
      int16_t cl0x = (int16_t)roundf((float)sxa - nx * hwa);
      int16_t cl0y = (int16_t)roundf((float)sya - ny * hwa);
      int16_t cr = (int16_t)roundf(chwa);
      if (cr >= 1) {
        gfx->fillCircle(cr0x, cr0y, cr, stepColor);
        gfx->fillCircle(cl0x, cl0y, cr, stepColor);
      }
    }
  }
}

// Draws a cross street perpendicular to the route at forward distance z_m with fade-away hair tails
static void draw3DCrossStreet(float z_m, float halfW_m, int16_t baseW, uint16_t color) {
  draw3DBorderRoad(0.0f, z_m, -halfW_m, z_m, baseW, color);
  draw3DBorderRoad(0.0f, z_m,  halfW_m, z_m, baseW, color);
}

// ===================== Vehicle Marker =====================
// Modern extra-rounded 3D arrowhead vehicle puck matching user design (larger than road with thick black stroke)
static void drawVehicleMarker() {
  const int16_t x = CX;
  const int16_t y = Y_CAR;

  // 1. Soft drop shadow on road surface beneath arrowhead
  gfx->fillRoundRect(x - 30, y - 16, 60, 42, 14, 0x0000);

  // 2. Outer black casing (thick 3-4px rounded shell):
  // Rounded corner caps for outer black casing (large radii for extra-round pill corners):
  gfx->fillCircle(x, y - 18, 12, 0x0000);        // Rounded dome apex (outer r=12)
  gfx->fillCircle(x - 22, y + 10, 11, 0x0000);   // Rounded left wing pill (outer r=11)
  gfx->fillCircle(x + 22, y + 10, 11, 0x0000);   // Rounded right wing pill (outer r=11)
  // Outer black body fill:
  gfx->fillTriangle(x, y - 18, x - 11, y - 23, x - 32, y + 5, 0x0000);
  gfx->fillTriangle(x, y - 18, x - 32, y + 5, x - 22, y + 10, 0x0000);
  gfx->fillTriangle(x, y - 18, x + 11, y - 23, x + 32, y + 5, 0x0000);
  gfx->fillTriangle(x, y - 18, x + 32, y + 5, x + 22, y + 10, 0x0000);
  gfx->fillTriangle(x, y - 18, x - 22, y + 10, x + 22, y + 10, 0x0000);

  // 5. Solid pure white inner arrowhead with extra-round corners (inset by 3.5px)
  // Rounded corner caps for inner white body:
  gfx->fillCircle(x, y - 18, 8, 0xFFFF);        // Large rounded dome nose (inner r=8)
  gfx->fillCircle(x - 22, y + 10, 7, 0xFFFF);   // Rounded left wing pill (inner r=7)
  gfx->fillCircle(x + 22, y + 10, 7, 0xFFFF);   // Rounded right wing pill (inner r=7)
  // White body fill between round caps:
  gfx->fillTriangle(x, y - 18, x - 7, y - 22, x - 28, y + 6, 0xFFFF);
  gfx->fillTriangle(x, y - 18, x - 28, y + 6, x - 22, y + 10, 0xFFFF);
  gfx->fillTriangle(x, y - 18, x + 7, y - 22, x + 28, y + 6, 0xFFFF);
  gfx->fillTriangle(x, y - 18, x + 28, y + 6, x + 22, y + 10, 0xFFFF);
  gfx->fillTriangle(x, y - 18, x - 22, y + 10, x, y + 4, 0xFFFF);
  gfx->fillTriangle(x, y - 18, x + 22, y + 10, x, y + 4, 0xFFFF);

  // 6. Rear rounded notch (carved clean curve with thick black stroke):
  gfx->fillCircle(x, y + 13, 9, 0x0000);
  gfx->drawLine(x - 22, y + 17, x, y + 7, 0x0000);
  gfx->drawLine(x - 22, y + 16, x, y + 6, 0x0000);
  gfx->drawLine(x - 22, y + 15, x, y + 5, 0x0000);
  gfx->drawLine(x + 22, y + 17, x, y + 7, 0x0000);
  gfx->drawLine(x + 22, y + 16, x, y + 6, 0x0000);
  gfx->drawLine(x + 22, y + 15, x, y + 5, 0x0000);
}

// ===================== Maneuver Classification Helpers =====================
static inline bool isManeuverSlightRight(const String &ml) {
  return (ml.indexOf("slight-right") >= 0 || ml.indexOf("slight_right") >= 0 ||
          ml.indexOf("slight right") >= 0 || ml.indexOf("slightly-right") >= 0 ||
          ml.indexOf("bear-right") >= 0 || ml.indexOf("bear_right") >= 0 ||
          ml.indexOf("bear right") >= 0 || ml.indexOf("keep-right") >= 0 ||
          ml.indexOf("keep_right") >= 0 || ml.indexOf("keep right") >= 0 ||
          ml.indexOf("fork-right") >= 0 || ml.indexOf("fork_right") >= 0 ||
          ml.indexOf("fork right") >= 0 || ml.indexOf("stay right") >= 0);
}

static inline bool isManeuverSlightLeft(const String &ml) {
  return (ml.indexOf("slight-left") >= 0 || ml.indexOf("slight_left") >= 0 ||
          ml.indexOf("slight left") >= 0 || ml.indexOf("slightly-left") >= 0 ||
          ml.indexOf("bear-left") >= 0 || ml.indexOf("bear_left") >= 0 ||
          ml.indexOf("bear left") >= 0 || ml.indexOf("keep-left") >= 0 ||
          ml.indexOf("keep_left") >= 0 || ml.indexOf("keep left") >= 0 ||
          ml.indexOf("fork-left") >= 0 || ml.indexOf("fork_left") >= 0 ||
          ml.indexOf("fork left") >= 0 || ml.indexOf("stay left") >= 0);
}

static inline bool isManeuverSharpRight(const String &ml) {
  return (ml.indexOf("sharp-right") >= 0 || ml.indexOf("sharp_right") >= 0 ||
          ml.indexOf("sharp right") >= 0 || ml.indexOf("hard-right") >= 0 ||
          ml.indexOf("hard_right") >= 0 || ml.indexOf("hard right") >= 0);
}

static inline bool isManeuverSharpLeft(const String &ml) {
  return (ml.indexOf("sharp-left") >= 0 || ml.indexOf("sharp_left") >= 0 ||
          ml.indexOf("sharp left") >= 0 || ml.indexOf("hard-left") >= 0 ||
          ml.indexOf("hard_left") >= 0 || ml.indexOf("hard left") >= 0);
}

// ===================== Maneuver Glyph Icons (3X Bold Vector) =====================
static void drawManeuverIcon3X(int16_t cx, int16_t cy, const String &m, uint16_t color) {
  String ml = m;
  ml.toLowerCase();

  if (ml.indexOf("arrive") >= 0 || ml.indexOf("ziel") >= 0) {
    // Concentric target destination marker
    gfx->fillCircle(cx, cy, 12, 0x07E0);
    gfx->drawCircle(cx, cy, 22, color);
    gfx->drawCircle(cx, cy, 23, color);
    gfx->drawCircle(cx, cy, 24, color);
    gfx->drawCircle(cx, cy, 34, color);
    gfx->drawCircle(cx, cy, 35, color);
    gfx->drawCircle(cx, cy, 36, color);

  } else if (ml.indexOf("uturn") >= 0 || ml.indexOf("u-turn") >= 0) {
    // 3X U-turn loop
    gfx->fillRect(cx + 12, cy - 6, 15, 48, color);
    gfx->fillRect(cx - 27, cy - 6, 15, 26, color);
    gfx->fillCircle(cx, cy - 12, 27, color);
    gfx->fillCircle(cx, cy - 12, 12, 0x0841);
    gfx->fillRect(cx - 27, cy - 6, 54, 16, 0x0841);
    gfx->fillRect(cx + 12, cy - 8, 15, 50, color);
    gfx->fillRect(cx - 27, cy - 8, 15, 28, color);
    gfx->fillTriangle(cx - 19, cy + 42, cx - 36, cy + 20, cx - 2, cy + 20, color);

  } else if (ml.indexOf("roundabout") >= 0 || ml.indexOf("kreis") >= 0) {
    // 3X Roundabout traffic circle with exit arrow
    for (int16_t rRing = 27; rRing <= 32; rRing++) {
      gfx->drawCircle(cx, cy, rRing, color);
    }
    gfx->fillRect(cx - 7, cy + 24, 15, 20, color);
    gfx->fillTriangle(cx + 32, cy - 12, cx + 44, cy + 10, cx + 18, cy + 10, color);

  } else if (isManeuverSharpRight(ml)) {
    // 3X Sharp right / hairpin (~135 degrees)
    gfx->fillRect(cx - 24, cy - 4, 15, 48, color);
    gfx->fillRoundRect(cx - 24, cy - 36, 52, 26, 12, color);
    gfx->fillTriangle(cx + 10, cy - 22, cx + 24, cy - 22, cx + 36, cy + 6, color);
    gfx->fillTriangle(cx + 10, cy - 22, cx + 36, cy + 6, cx + 22, cy + 6, color);
    gfx->fillTriangle(cx + 44, cy + 20, cx + 16, cy + 12, cx + 34, cy - 10, color);

  } else if (isManeuverSharpLeft(ml)) {
    // 3X Sharp left / hairpin (~135 degrees)
    gfx->fillRect(cx + 9, cy - 4, 15, 48, color);
    gfx->fillRoundRect(cx - 28, cy - 36, 52, 26, 12, color);
    gfx->fillTriangle(cx - 10, cy - 22, cx - 24, cy - 22, cx - 36, cy + 6, color);
    gfx->fillTriangle(cx - 10, cy - 22, cx - 36, cy + 6, cx - 22, cy + 6, color);
    gfx->fillTriangle(cx - 44, cy + 20, cx - 16, cy + 12, cx - 34, cy - 10, color);

  } else if (isManeuverSlightRight(ml)) {
    // 3X 45-degree fork / bear right
    // 1. Dim continuation road straight ahead
    gfx->fillRoundRect(cx - 24, cy - 32, 14, 46, 4, 0x4208);
    // 2. Base stem coming up from bottom
    gfx->fillRoundRect(cx - 24, cy + 10, 14, 34, 4, color);
    // 3. Diagonal branch angling 45 degrees toward top-right
    gfx->fillTriangle(cx - 24, cy + 8, cx - 10, cy + 22, cx + 24, cy - 12, color);
    gfx->fillTriangle(cx - 24, cy + 8, cx + 24, cy - 12, cx + 10, cy - 26, color);
    // 4. Large bold arrowhead pointing northeast (45 degrees)
    gfx->fillTriangle(cx + 42, cy - 42, cx + 4, cy - 34, cx + 34, cy - 4, color);

  } else if (isManeuverSlightLeft(ml)) {
    // 3X 45-degree fork / bear left
    // 1. Dim continuation road straight ahead
    gfx->fillRoundRect(cx + 10, cy - 32, 14, 46, 4, 0x4208);
    // 2. Base stem coming up from bottom
    gfx->fillRoundRect(cx + 10, cy + 10, 14, 34, 4, color);
    // 3. Diagonal branch angling 45 degrees toward top-left
    gfx->fillTriangle(cx + 24, cy + 8, cx + 10, cy + 22, cx - 24, cy - 12, color);
    gfx->fillTriangle(cx + 24, cy + 8, cx - 24, cy - 12, cx - 10, cy - 26, color);
    // 4. Large bold arrowhead pointing northwest (45 degrees)
    gfx->fillTriangle(cx - 42, cy - 42, cx - 4, cy - 34, cx - 34, cy - 4, color);

  } else if (ml.indexOf("right") >= 0 || ml.indexOf("rechts") >= 0) {
    // 3X 90-degree right turn
    gfx->fillRect(cx - 26, cy - 6, 16, 50, color);
    gfx->fillRoundRect(cx - 26, cy - 28, 48, 28, 10, color);
    gfx->fillRect(cx - 2, cy - 26, 30, 16, color);
    gfx->fillTriangle(cx + 44, cy - 18, cx + 14, cy - 40, cx + 14, cy + 4, color);

  } else if (ml.indexOf("left") >= 0 || ml.indexOf("links") >= 0) {
    // 3X 90-degree left turn
    gfx->fillRect(cx + 10, cy - 6, 16, 50, color);
    gfx->fillRoundRect(cx - 22, cy - 28, 48, 28, 10, color);
    gfx->fillRect(cx - 28, cy - 26, 30, 16, color);
    gfx->fillTriangle(cx - 44, cy - 18, cx - 14, cy - 40, cx - 14, cy + 4, color);

  } else if (ml.indexOf("merge") >= 0) {
    // 3X Highway merge
    gfx->fillRect(cx - 22, cy - 28, 14, 72, color);
    gfx->fillTriangle(cx - 15, cy - 44, cx - 34, cy - 16, cx + 4, cy - 16, color);
    gfx->fillTriangle(cx + 26, cy + 34, cx + 36, cy + 24, cx - 8, cy - 4, color);
    gfx->fillTriangle(cx + 26, cy + 34, cx - 8, cy - 4, cx - 18, cy + 6, color);

  } else {
    // 3X Default: Straight navigation arrow
    gfx->fillRoundRect(cx - 8, cy - 10, 16, 52, 4, color);
    gfx->fillTriangle(cx, cy - 44, cx - 32, cy - 10, cx + 32, cy - 10, color);
  }
}

// ===================== Floating Maneuver Arrow (3X Big, Borderless) =====================
// Positioned at top-left, just below the top HUD bar (Y_HORIZON = 76)
static void drawFloatingManeuverCard() {
  const int16_t cx = 66;
  const int16_t cy = Y_HORIZON + 8 + 52; // 136

  drawManeuverIcon3X(cx, cy, g_maneuver, 0xFFFF);
}

// ===================== 3D Road Network Rendering =====================
static float s_animRoadOffsetZ = 0.0f;
static float s_animDashOffset = 0.0f;
static uint32_t s_lastAnimFrameMs = 0;
static uint32_t s_lastSeenVectorMs = 0;

static void draw3DRoadNetwork() {
  uint32_t now = millis();

  // Reset dead-reckoning offset to 0 when a fresh vector packet arrives from mobile
  if (g_lastRoadVectorTimeMs != s_lastSeenVectorMs) {
    s_lastSeenVectorMs = g_lastRoadVectorTimeMs;
    s_animRoadOffsetZ = 0.0f;
    s_lastAnimFrameMs = now;
  }

  // Smooth dead-reckoning advance at 20-24 FPS based on vehicle speed
  if (s_lastAnimFrameMs == 0) s_lastAnimFrameMs = now;
  float dt = (float)(now - s_lastAnimFrameMs) * 0.001f;
  s_lastAnimFrameMs = now;
  if (dt > 0.1f) dt = 0.041f; // clamp

  if (g_vehicleSpeedKmh > 1.0f) {
    float speedMps = g_vehicleSpeedKmh * (1000.0f / 3600.0f);
    s_animRoadOffsetZ += speedMps * dt;
    if (s_animRoadOffsetZ > 35.0f) s_animRoadOffsetZ = 35.0f;

    s_animDashOffset += speedMps * dt;
    if (s_animDashOffset >= 16.0f) {
      s_animDashOffset = fmodf(s_animDashOffset, 16.0f);
    }
  }

  // If real map vector road segments are provided by the mobile app, render them:
  if (g_numRoadSegments > 0) {
    uint8_t count = g_numRoadSegments;

    // Pass 1: Non-route road double borders with hair-strand fade-away (top-to-bottom)
    for (int i = (int)count - 1; i >= 0; i--) {
      if (g_roadSegments[i].type == 0) {
        float z0 = (float)g_roadSegments[i].z0 - s_animRoadOffsetZ;
        float z1 = (float)g_roadSegments[i].z1 - s_animRoadOffsetZ;
        draw3DBorderRoad((float)g_roadSegments[i].x0, z0,
                         (float)g_roadSegments[i].x1, z1,
                         ROAD_W_SIDE, COL_MAP_GRAY);
      }
    }

    // Pass 2A: Active route soft AA outer border (top-to-bottom)
    for (int i = (int)count - 1; i >= 0; i--) {
      if (g_roadSegments[i].type == 1) {
        float z0 = (float)g_roadSegments[i].z0 - s_animRoadOffsetZ;
        float z1 = (float)g_roadSegments[i].z1 - s_animRoadOffsetZ;
        draw3DRibbonRaw((float)g_roadSegments[i].x0, z0,
                        (float)g_roadSegments[i].x1, z1,
                        (float)ROAD_W_ACTIVE, COL_MAP_ROUTE_AA, 1.0f);
      }
    }

    // Pass 2B: Active route solid pure white core (top-to-bottom, never overwritten by AA)
    for (int i = (int)count - 1; i >= 0; i--) {
      if (g_roadSegments[i].type == 1) {
        float z0 = (float)g_roadSegments[i].z0 - s_animRoadOffsetZ;
        float z1 = (float)g_roadSegments[i].z1 - s_animRoadOffsetZ;
        draw3DRibbonRaw((float)g_roadSegments[i].x0, z0,
                        (float)g_roadSegments[i].x1, z1,
                        (float)ROAD_W_ACTIVE, COL_MAP_ROUTE, 0.0f);
      }
    }

    return;
  }

  // Fallback: procedural maneuver preview when vector geometry is not streaming
  // Parse remaining distance to next maneuver
  int distM = parseDistanceMeters(g_distance);
  if (distM < 0) distM = 150;

  // Map real distance into visible 3D ground depth (12m..230m ahead)
  float zTurn = (float)constrain(distM, 12, 230);
  const float zFar = 260.0f;
  const float crossArm = 120.0f;

  // 1. Render intermediate cross streets with double road borders
  if (zTurn > 75.0f) {
    draw3DCrossStreet(50.0f, crossArm, ROAD_W_SIDE, COL_MAP_GRAY);
  }
  if (zTurn > 135.0f) {
    draw3DCrossStreet(110.0f, crossArm, ROAD_W_SIDE, COL_MAP_GRAY);
  }
  if (zTurn > 195.0f) {
    draw3DCrossStreet(170.0f, crossArm, ROAD_W_SIDE, COL_MAP_GRAY);
  }

  // 2. Render maneuver intersection in 3D perspective
  String ml = g_maneuver;
  ml.toLowerCase();

  if (isManeuverSlightRight(ml)) {
    // Non-route straight road and left arm (double border)
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DBorderRoad(0.0f, zTurn, -crossArm, zTurn, ROAD_W_SIDE, COL_MAP_GRAY);

    // Active route: straight stem (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Active route: 45 degree fork right (solid white)
    draw3DSegment(0.0f, zTurn, 55.0f, zTurn + 50.0f, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (isManeuverSlightLeft(ml)) {
    // Non-route straight road and right arm (double border)
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DBorderRoad(0.0f, zTurn, crossArm, zTurn, ROAD_W_SIDE, COL_MAP_GRAY);

    // Active route: straight stem (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Active route: 45 degree fork left (solid white)
    draw3DSegment(0.0f, zTurn, -55.0f, zTurn + 50.0f, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (isManeuverSharpRight(ml)) {
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DBorderRoad(0.0f, zTurn, -crossArm, zTurn, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    draw3DSegment(0.0f, zTurn, 70.0f, zTurn - 20.0f, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (isManeuverSharpLeft(ml)) {
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DBorderRoad(0.0f, zTurn, crossArm, zTurn, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    draw3DSegment(0.0f, zTurn, -70.0f, zTurn - 20.0f, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (ml.indexOf("roundabout") >= 0 || ml.indexOf("kreis") >= 0) {
    const float rR = 24.0f;
    // Straight approach stem to roundabout entry (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn - rR, ROAD_W_ACTIVE, COL_MAP_ROUTE);

    // Non-taken roundabout exits (double border)
    draw3DBorderRoad(-rR, zTurn, -(crossArm + 10.0f), zTurn, ROAD_W_SIDE, COL_MAP_GRAY);
    draw3DBorderRoad(0.0f, zTurn + rR, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);

    // Roundabout ring in 3D perspective
    const int nPts = 12;
    for (int i = 0; i < nPts; i++) {
      float a0 = (float)i * (2.0f * 3.14159f / (float)nPts);
      float a1 = (float)(i + 1) * (2.0f * 3.14159f / (float)nPts);
      float px0 = rR * sinf(a0);
      float pz0 = zTurn + rR * cosf(a0);
      float px1 = rR * sinf(a1);
      float pz1 = zTurn + rR * cosf(a1);

      if (px0 >= -2.0f && px1 >= -2.0f) {
        draw3DSegment(px0, pz0, px1, pz1, ROAD_W_SIDE, COL_MAP_ROUTE);
      } else {
        draw3DBorderRoad(px0, pz0, px1, pz1, ROAD_W_SIDE, COL_MAP_GRAY);
      }
    }
    // Taken right exit arm (solid white)
    draw3DSegment(rR, zTurn, crossArm + 10.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (ml.indexOf("uturn") >= 0 || ml.indexOf("u-turn") >= 0) {
    // Non-route straight ahead (double border)
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);

    // Route: approach stem (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Route: U-turn loop apex & return branch (solid white)
    draw3DSegment(0.0f, zTurn, -24.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    draw3DSegment(-24.0f, zTurn, -24.0f, zTurn - 40.0f, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (ml.indexOf("arrive") >= 0 || ml.indexOf("ziel") >= 0) {
    // Route straight to destination target (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Destination concentric ring target
    int16_t dx, dy;
    float dhw;
    project3D(0.0f, zTurn, dx, dy, dhw, 18.0f);
    int16_t dw = (int16_t)roundf(dhw);
    gfx->fillCircle(dx, dy, dw, 0xFFFF);
    gfx->drawCircle(dx, dy, dw + 4, 0xFFFF);
    gfx->drawCircle(dx, dy, dw + 5, 0xFFFF);

  } else if (ml.indexOf("right") >= 0 || ml.indexOf("rechts") >= 0) {
    // Non-route continuation straight ahead (double border)
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);
    // Non-route left cross street (double border)
    draw3DBorderRoad(0.0f, zTurn, -crossArm, zTurn, ROAD_W_SIDE, COL_MAP_GRAY);

    // Active route: straight stem from vehicle to intersection (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Active route: right turn arm (solid white)
    draw3DSegment(0.0f, zTurn, 80.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else if (ml.indexOf("left") >= 0 || ml.indexOf("links") >= 0) {
    // Non-route continuation straight ahead (double border)
    draw3DBorderRoad(0.0f, zTurn, 0.0f, zFar, ROAD_W_SIDE, COL_MAP_GRAY);
    // Non-route right cross street (double border)
    draw3DBorderRoad(0.0f, zTurn, crossArm, zTurn, ROAD_W_SIDE, COL_MAP_GRAY);

    // Active route: straight stem from vehicle to intersection (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Active route: left turn arm (solid white)
    draw3DSegment(0.0f, zTurn, -80.0f, zTurn, ROAD_W_ACTIVE, COL_MAP_ROUTE);

  } else {
    // Default straight road through city blocks (solid white)
    draw3DSegment(0.0f, 0.0f, 0.0f, zFar, ROAD_W_ACTIVE, COL_MAP_ROUTE);
    // Distant cross street at 230m (double border)
    draw3DCrossStreet(230.0f, crossArm, ROAD_W_SIDE, COL_MAP_GRAY);
  }
}

// ===================== Weather Widget (Top-Right HUD) =====================
static void drawWeatherWidget(int16_t cx, int16_t cy) {
  String cond = g_weatherCond;
  cond.toLowerCase();

  if (cond.indexOf("sun") >= 0 || cond.indexOf("clear") >= 0) {
    // 1. Sunny / Clear: Bright yellow sun with radiant rays
    const uint16_t sunCol = 0xFDE0;
    gfx->fillCircle(cx, cy, 7, sunCol);
    const int8_t r1 = 9;
    const int8_t r2 = 13;
    gfx->drawLine(cx, cy - r1, cx, cy - r2, sunCol);
    gfx->drawLine(cx, cy + r1, cx, cy + r2, sunCol);
    gfx->drawLine(cx - r1, cy, cx - r2, cy, sunCol);
    gfx->drawLine(cx + r1, cy, cx + r2, cy, sunCol);
    gfx->drawLine(cx - 6, cy - 6, cx - 9, cy - 9, sunCol);
    gfx->drawLine(cx + 6, cy - 6, cx + 9, cy - 9, sunCol);
    gfx->drawLine(cx - 6, cy + 6, cx - 9, cy + 9, sunCol);
    gfx->drawLine(cx + 6, cy + 6, cx + 9, cy + 9, sunCol);

  } else if (cond.indexOf("pcloud") >= 0 || cond.indexOf("partly") >= 0) {
    // 2. Partly Cloudy: Sun peeking behind cloud
    const uint16_t sunCol = 0xFDE0;
    const uint16_t cldCol = 0xD6BA;
    gfx->fillCircle(cx + 4, cy - 5, 5, sunCol);
    gfx->drawLine(cx + 4, cy - 11, cx + 4, cy - 13, sunCol);
    gfx->drawLine(cx + 10, cy - 5, cx + 12, cy - 5, sunCol);
    gfx->drawLine(cx + 8, cy - 9, cx + 10, cy - 11, sunCol);
    gfx->fillCircle(cx - 5, cy + 2, 5, cldCol);
    gfx->fillCircle(cx + 1, cy - 1, 6, cldCol);
    gfx->fillCircle(cx + 7, cy + 3, 4, cldCol);
    gfx->fillRoundRect(cx - 8, cy + 1, 18, 6, 3, cldCol);

  } else if (cond.indexOf("rain") >= 0 || cond.indexOf("shower") >= 0 || cond.indexOf("drizzle") >= 0) {
    // 3. Rain: Slate cloud with bright sky-blue rain drops
    const uint16_t cldCol  = 0x9CF3;
    const uint16_t dropCol = 0x5DFB;
    gfx->fillCircle(cx - 5, cy - 2, 5, cldCol);
    gfx->fillCircle(cx + 1, cy - 5, 7, cldCol);
    gfx->fillCircle(cx + 7, cy - 1, 5, cldCol);
    gfx->fillRoundRect(cx - 8, cy - 3, 18, 7, 3, cldCol);
    gfx->drawLine(cx - 5, cy + 6, cx - 7, cy + 12, dropCol);
    gfx->drawLine(cx,     cy + 6, cx - 2, cy + 12, dropCol);
    gfx->drawLine(cx + 5, cy + 6, cx + 3, cy + 12, dropCol);

  } else if (cond.indexOf("thunder") >= 0 || cond.indexOf("storm") >= 0) {
    // 4. Thunderstorm: Dark storm cloud with lightning bolt
    const uint16_t cldCol  = 0x632C;
    const uint16_t boltCol = 0xFFE0;
    gfx->fillCircle(cx - 5, cy - 2, 5, cldCol);
    gfx->fillCircle(cx + 1, cy - 5, 7, cldCol);
    gfx->fillCircle(cx + 7, cy - 1, 5, cldCol);
    gfx->fillRoundRect(cx - 8, cy - 3, 18, 7, 3, cldCol);
    gfx->drawLine(cx + 1, cy + 4, cx - 2, cy + 9,  boltCol);
    gfx->drawLine(cx - 2, cy + 9, cx + 2, cy + 9,  boltCol);
    gfx->drawLine(cx + 2, cy + 9, cx - 1, cy + 15, boltCol);

  } else if (cond.indexOf("snow") >= 0) {
    // 5. Snow: Cloud with white snowflakes
    const uint16_t cldCol = 0xCE79;
    gfx->fillCircle(cx - 5, cy - 2, 5, cldCol);
    gfx->fillCircle(cx + 1, cy - 5, 7, cldCol);
    gfx->fillCircle(cx + 7, cy - 1, 5, cldCol);
    gfx->fillRoundRect(cx - 8, cy - 3, 18, 7, 3, cldCol);
    gfx->fillCircle(cx - 5, cy + 8, 1, 0xFFFF);
    gfx->fillCircle(cx,     cy + 10, 1, 0xFFFF);
    gfx->fillCircle(cx + 5, cy + 8, 1, 0xFFFF);

  } else if (cond.indexOf("fog") >= 0 || cond.indexOf("mist") >= 0) {
    // 6. Fog / Mist: Horizontal stacked bars
    const uint16_t fogCol = 0xAD75;
    gfx->fillRoundRect(cx - 10, cy - 6, 20, 3, 1, fogCol);
    gfx->fillRoundRect(cx - 12, cy - 1, 24, 3, 1, fogCol);
    gfx->fillRoundRect(cx - 8,  cy + 4, 16, 3, 1, fogCol);

  } else {
    // 7. Overcast / Cloudy: Full puffy cloud
    const uint16_t cldCol = 0xCE79;
    gfx->fillCircle(cx - 6, cy - 1, 6, cldCol);
    gfx->fillCircle(cx + 1, cy - 4, 8, cldCol);
    gfx->fillCircle(cx + 8, cy + 0, 5, cldCol);
    gfx->fillRoundRect(cx - 9, cy - 1, 20, 7, 3, cldCol);
  }

  // Temperature text below icon (e.g. "28°")
  if (g_weatherTemp.length() > 0) {
    int16_t x1, y1;
    uint16_t tw, th;
    gfx->setFont(&SFCompactBold9pt7b);
    gfx->getTextBounds(g_weatherTemp, 0, 0, &x1, &y1, &tw, &th);
    gfx->setTextColor(0xFFFF);
    gfx->setCursor(cx - (tw / 2) - x1, cy + 28);
    gfx->print(g_weatherTemp);
  }
}

// ===================== Top Floating HUD (Beeline Moto 2) =====================
static void drawTopHUD() {
  gfx->fillRect(0, 0, SCREEN_W, Y_HORIZON, COL_BG);
  gfx->drawFastHLine(12, Y_HORIZON - 1, SCREEN_W - 24, COL_CARD_LINE);

  // Large distance digits & prominent street name (starting at x=20 for clean left-alignment)
  String distStr = g_distance.length() > 0 ? g_distance : "0 m";
  String streetStr = g_street.length() > 0 ? g_street : "Route";

  // Big 24pt turn distance numbers (easily readable at vehicle distance)
  gfx->setFont(&SFCompactBold24pt7b);
  gfx->setTextColor(0xFFFF);
  gfx->setCursor(20, 40);
  gfx->print(distStr);

  // Big 12pt street name (fits cleanly up to weather widget)
  String fitS = fitStreet(streetStr, &SFCompactBold12pt7b, 275);
  gfx->setFont(&SFCompactBold12pt7b);
  gfx->setTextColor(COL_DIM_WHITE);
  gfx->setCursor(20, 64);
  gfx->print(fitS);

  // Top-Right: Weather icon & temperature for current location
  drawWeatherWidget(332, 20);
}

// ===================== Turn Warning Strobe LED =====================
// Flashes a double-blink green strobe at top-center when turn distance is < 50m
static void drawTurnIndicatorLED() {
  if (g_maneuver.length() == 0 || g_maneuver == "clear" || g_maneuver == "end" || g_maneuver == "straight") {
    return;
  }

  String dLow = g_distance;
  dLow.toLowerCase();
  bool isUnder50 = false;
  if (dLow.indexOf("< 50") >= 0 || dLow.indexOf("now") >= 0 || dLow == "0 m") {
    isUnder50 = true;
  } else {
    int distM = parseDistanceMeters(g_distance);
    if (distM >= 0 && distM <= 50) {
      isUnder50 = true;
    }
  }

  if (!isUnder50) return;

  // Automotive double-blink strobe: 800ms cycle
  // 0..130ms: ON, 130..220ms: OFF, 220..350ms: ON, 350..800ms: OFF pause
  uint32_t phase = millis() % 800;
  bool flashOn = (phase < 130) || (phase >= 220 && phase < 350);

  // Prominent wide rounded lightbar (84x16px) centered at top
  const int16_t barW = 84;
  const int16_t barH = 16;
  const int16_t barX = CX - (barW / 2); // 142
  const int16_t barY = 8;
  const int16_t r    = 8; // fully rounded ends (pill / curved rectangle)

  if (flashOn) {
    // High-intensity all-green neon strobe lightbar
    gfx->fillRoundRect(barX - 4, barY - 3, barW + 8, barH + 6, r + 3, 0x0320); // Soft ambient green halo
    gfx->fillRoundRect(barX - 2, barY - 1, barW + 4, barH + 2, r + 1, 0x05E0); // Medium bright green aura
    gfx->fillRoundRect(barX,     barY,     barW,     barH,     r,     0x07E0); // Pure vivid neon green core
  } else {
    // Dim indicator bezel during blink pauses
    gfx->fillRoundRect(barX, barY, barW, barH, r, 0x0841);
    gfx->drawRoundRect(barX, barY, barW, barH, r, 0x18E3);
  }
}

// ===================== Route Distance Remaining Floating HUD =====================
// Positioned at bottom-right, just above the bottom HUD deck (Y_BOTTOM_HUD = 386)
static void drawRouteDistanceHUD() {
  if (g_navTotalDist.length() == 0) return;

  int16_t x1, y1;
  uint16_t textW, textH;
  gfx->setFont(&SFCompactBold16pt7b);
  gfx->getTextBounds(g_navTotalDist, 0, 0, &x1, &y1, &textW, &textH);

  const int16_t padH  = 12;
  const int16_t padV  = 6;
  const int16_t cardW = textW + (padH * 2);
  const int16_t cardH = textH + (padV * 2);
  const int16_t cardX = SCREEN_W - 14 - cardW;
  const int16_t cardY = Y_BOTTOM_HUD - cardH - 8;
  const int16_t cardR = 8;

  gfx->fillRoundRect(cardX, cardY, cardW, cardH, cardR, 0x0841);
  gfx->drawRoundRect(cardX, cardY, cardW, cardH, cardR, COL_CARD_LINE);

  gfx->setTextColor(0xFFFF);
  gfx->setCursor(cardX + padH, cardY + cardH - padV - 2);
  gfx->print(g_navTotalDist);
}

// ===================== Bottom HUD (Beeline Moto 2) =====================
static void drawBottomHUD() {
  gfx->fillRect(0, Y_BOTTOM_HUD, SCREEN_W, SCREEN_H - Y_BOTTOM_HUD, COL_BG);
  gfx->drawFastHLine(12, Y_BOTTOM_HUD, SCREEN_W - 24, COL_CARD_LINE);

  // Remaining Progress Bar
  float p = constrain(g_navProgress, 0.0f, 1.0f);
  int16_t barW = SCREEN_W - 36;
  int16_t barX = 18;
  int16_t barY = Y_BOTTOM_HUD + 8;

  // Track in dim gray
  gfx->fillRoundRect(barX, barY, barW, 5, 2, COL_CARD_LINE);
  // Active progress in solid white
  int16_t fillW = (int16_t)roundf((float)barW * p);
  if (fillW > 0) {
    gfx->fillRoundRect(barX, barY, max((int16_t)5, fillW), 5, 2, 0xFFFF);
  }

  const int16_t textBaseline = Y_BOTTOM_HUD + 38;

  // Left: ETA in 12pt (large, high-contrast)
  gfx->setFont(&SFCompactBold12pt7b);
  gfx->setTextColor(COL_DIM_WHITE);
  String etaText = (g_navEta.length() > 0) ? ("ETA " + g_navEta) : "ETA --:--";
  gfx->setCursor(18, textBaseline);
  gfx->print(etaText);

  // Right: Battery in 12pt
  if (g_batLevel >= 0) {
    String batStr = String(g_batLevel) + "%";
    int16_t x1, y1;
    uint16_t w, h;
    gfx->setFont(&SFCompactBold12pt7b);
    gfx->getTextBounds(batStr, 0, 0, &x1, &y1, &w, &h);
    gfx->setTextColor((g_batLevel <= 20 && !g_batCharge) ? COL_RED : COL_DIM_WHITE);
    gfx->setCursor(SCREEN_W - 18 - w, textBaseline);
    gfx->print(batStr);
  }
}

// ===================== Public Entry Point =====================
void screen_map_draw() {
  bool hasNav = g_maneuver.length() > 0 &&
                g_maneuver != "clear" && g_maneuver != "end";

  if (!hasNav) {
    gfx->fillScreen(COL_BG);
    if (g_navEnded) {
      drawTextC("ARRIVED", CY - 20, &SFCompactBold24pt7b, 0xFFFF);
      drawTextC("Navigation ended", CY + 25, &SFCompactBold12pt7b, COL_DIM_WHITE);
    } else {
      drawTextC("3D MINI MAP", CY - 20, &SFCompactBold16pt7b, 0xFFFF);
      if (g_connected) {
        drawTextC("WAITING FOR ROUTE", CY + 18, &SFCompactBold12pt7b, 0x8410);
      } else {
        drawTextC("WAITING FOR BLE", CY + 18, &SFCompactBold12pt7b, 0x8410);
      }
    }
    return;
  }

  // Clear 3D road viewport (middle canvas between top HUD and bottom HUD)
  gfx->fillRect(0, Y_HORIZON, SCREEN_W, Y_BOTTOM_HUD - Y_HORIZON, COL_BG);

  // 1. Render 3D 40-degree perspective road network
  draw3DRoadNetwork();

  // 2. Render pinned 3D vehicle chevron on the road
  drawVehicleMarker();

  // 3. Render Beeline Moto 2 top floating HUD
  drawTopHUD();

  // 4. Render floating maneuver arrow card (top-left, just below top bar)
  drawFloatingManeuverCard();

  // 5. Render top center flashing turn indicator (<50m)
  drawTurnIndicatorLED();

  // 6. Render floating route distance remaining card (bottom-right, above bottom HUD)
  drawRouteDistanceHUD();

  // 7. Render bottom status & progress deck
  drawBottomHUD();
}

