/*
 * nav_arrows.h — Anti-aliased maneuver arrow bitmaps extracted directly
 *                from Figma design (figma_drive_assistant_screens 1.png).
 */

#pragma once
#include <Arduino.h>

struct NavManeuverBitmap {
  const char     *name;
  int16_t         x;        // Screen X position
  int16_t         y;        // Screen Y position
  int16_t         w;        // Bitmap width
  int16_t         h;        // Bitmap height
  const uint8_t  *alpha;    // 8-bit alpha mask (w * h) in PROGMEM (or nullptr)
  const uint16_t *rgb565;   // 16-bit RGB565 (w * h) in PROGMEM (or nullptr)
};

const NavManeuverBitmap* getNavManeuverBitmap(const String &m);
