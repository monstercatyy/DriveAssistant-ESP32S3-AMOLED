/*
 * ui_common.cpp — Shared UI state definitions and text rendering helpers.
 */

#include "ui_common.h"

Arduino_GFX    *gfx      = nullptr;
Arduino_Canvas *g_canvas = nullptr;

bool     g_connected   = false;
uint8_t  g_screen      = SCR_MAIN;

String   g_maneuver    = "";
String   g_distance    = "";
String   g_street      = "";
bool     g_navEnded    = false;
bool     g_navShown    = false;
uint8_t  g_iconBits[ICON_W * ICON_H / 8];
bool     g_iconValid   = false;
uint16_t *g_iconCanvas = nullptr;

uint8_t  g_mediaState  = 0;
String   g_mediaTitle  = "";
String   g_mediaArtist = "";

int      g_batLevel     = -1;
bool     g_batCharge    = false;
bool     g_hasBatteryIC = false;

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
