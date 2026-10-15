/*
 * ui_common.cpp - Shared UI state definitions and text rendering helpers.
 */

#include "ui_common.h"

Arduino_GFX    *gfx      = nullptr;
Arduino_Canvas *g_canvas = nullptr;

bool     g_connected   = false;
uint8_t  g_screen      = SCR_MAIN;

String   g_maneuver    = "";
String   g_distance    = "";
String   g_street      = "";
float    g_navProgress = 0.0f;
int      g_maxLegMeters = -1;
bool     g_navEnded    = false;
bool     g_navShown    = false;
String   g_navEta      = "";
String   g_navTotalDist = "";
uint8_t  g_iconBits[ICON_W * ICON_H / 8];
bool     g_iconValid   = false;
uint16_t *g_iconCanvas = nullptr;
String   g_weatherCond = "sun";
String   g_weatherTemp = "28°";

uint8_t  g_mediaState  = 0;
String   g_mediaTitle  = "";
String   g_mediaArtist = "";

int      g_batLevel     = -1;
bool     g_batCharge    = false;
bool     g_hasBatteryIC = false;
bool     g_hasImu       = false;
bool     g_imuMoving    = false;

bool     g_dirtyAll    = true;
bool     g_dirtyIcon   = false;
bool     g_dirtyDist   = false;
bool     g_dirtyStreet = false;
bool     g_dirtyMedia  = false;

void flushDisplay() {
  if (g_canvas && gfx == g_canvas) {
    g_canvas->flush();
  }
}

void drawTextC(const String &s, int16_t centerY, const GFXfont *font,
               uint16_t color) {
  int16_t x1, y1;
  uint16_t w, h;
  gfx->setFont(font);
  gfx->setTextSize(1);
  gfx->getTextBounds(s, 0, 0, &x1, &y1, &w, &h);
  gfx->setTextColor(color);
  gfx->setCursor(CX - w / 2 - x1, centerY - y1 - h / 2);
  gfx->print(s);
}

String fitStreet(String s, const GFXfont *font, uint16_t maxW) {
  int16_t x1, y1;
  uint16_t w, h;
  gfx->setFont(font);
  gfx->setTextSize(1);
  gfx->getTextBounds(s, 0, 0, &x1, &y1, &w, &h);
  if (w <= maxW) return s;

  while (s.length() > 1) {
    s.remove(s.length() - 1);
    gfx->getTextBounds(s + "...", 0, 0, &x1, &y1, &w, &h);
    if (w <= maxW) break;
  }
  return s + "...";
}

int parseDistanceMeters(const String &d) {
  if (d.length() == 0) return -1;
  String low = d;
  low.toLowerCase();
  low.replace(",", ".");
  float val = low.toFloat();
  if (val <= 0.0f && low[0] != '0') return -1;
  if (low.indexOf("km") >= 0) return (int)(val * 1000.0f);
  if (low.indexOf("mi") >= 0) return (int)(val * 1609.34f);
  if (low.indexOf("ft") >= 0) return (int)(val * 0.3048f);
  if (low.indexOf("yd") >= 0) return (int)(val * 0.9144f);
  return (int)val;
}

// ===================== Road Vector Geometry =====================
RoadSegment g_roadSegments[MAX_ROAD_SEGMENTS];
uint8_t     g_numRoadSegments = 0;
float       g_vehicleSpeedKmh = 0.0f;
uint32_t    g_lastRoadVectorTimeMs = 0;

void setRoadVectors(const String &vectorPayload) {
  int p1 = vectorPayload.indexOf('|');
  if (p1 < 0) return;
  int p2 = vectorPayload.indexOf('|', p1 + 1);
  if (p2 < 0) return;

  uint8_t count = (uint8_t)vectorPayload.substring(p1 + 1, p2).toInt();
  if (count > MAX_ROAD_SEGMENTS) count = MAX_ROAD_SEGMENTS;

  int p3 = vectorPayload.indexOf('|', p2 + 1);
  String data;
  float speed = g_vehicleSpeedKmh;
  if (p3 >= 0) {
    speed = vectorPayload.substring(p2 + 1, p3).toFloat();
    data = vectorPayload.substring(p3 + 1);
  } else {
    data = vectorPayload.substring(p2 + 1);
  }

  RoadSegment staging[MAX_ROAD_SEGMENTS];
  uint8_t stagingCount = 0;

  int start = 0;
  for (uint8_t i = 0; i < count; i++) {
    int end = data.indexOf(';', start);
    String segStr = (end >= 0) ? data.substring(start, end) : data.substring(start);
    if (segStr.length() == 0) break;

    int c1 = segStr.indexOf(',');
    int c2 = (c1 >= 0) ? segStr.indexOf(',', c1 + 1) : -1;
    int c3 = (c2 >= 0) ? segStr.indexOf(',', c2 + 1) : -1;
    int c4 = (c3 >= 0) ? segStr.indexOf(',', c3 + 1) : -1;

    if (c1 >= 0 && c2 >= 0 && c3 >= 0 && c4 >= 0) {
      staging[stagingCount].x0   = segStr.substring(0, c1).toInt();
      staging[stagingCount].z0   = segStr.substring(c1 + 1, c2).toInt();
      staging[stagingCount].x1   = segStr.substring(c2 + 1, c3).toInt();
      staging[stagingCount].z1   = segStr.substring(c3 + 1, c4).toInt();
      staging[stagingCount].type = (uint8_t)segStr.substring(c4 + 1).toInt();
      stagingCount++;
    }

    if (end < 0) break;
    start = end + 1;
  }

  // Atomic update to prevent dual-core display tear mid-frame
  static portMUX_TYPE s_roadVectorMux = portMUX_INITIALIZER_UNLOCKED;
  portENTER_CRITICAL(&s_roadVectorMux);
  memcpy(g_roadSegments, staging, sizeof(RoadSegment) * stagingCount);
  g_numRoadSegments = stagingCount;
  g_vehicleSpeedKmh = speed;
  g_lastRoadVectorTimeMs = millis();
  portEXIT_CRITICAL(&s_roadVectorMux);
}

void clearRoadVectors() {
  static portMUX_TYPE s_roadVectorMux = portMUX_INITIALIZER_UNLOCKED;
  portENTER_CRITICAL(&s_roadVectorMux);
  g_numRoadSegments = 0;
  g_vehicleSpeedKmh = 0.0f;
  g_lastRoadVectorTimeMs = millis();
  portEXIT_CRITICAL(&s_roadVectorMux);
}
