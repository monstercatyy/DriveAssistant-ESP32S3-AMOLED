/*
 * ui_common.h — Shared UI constants, state, and text rendering helpers
 *               used across all screen components.
 */

#pragma once

#include <Arduino.h>
#include "Arduino_GFX_Library.h"
#include "canvas/Arduino_Canvas.h"
#include "pin_config.h"
#include "fonts/SFCompactBold9pt7b.h"
#include "fonts/SFCompactBold12pt7b.h"
#include "fonts/SFCompactBold16pt7b.h"
#include "fonts/SFCompactBold24pt7b.h"
#include "fonts/SFCompactBold48pt7b.h"
#include "fonts/FreeSans9pt7b.h"
#include "fonts/FreeSansBold12pt7b.h"
#include "fonts/FreeSansBold24pt7b.h"

#define FW_VERSION      "1.8.0-S3"

// ===================== Display Dimensions =====================
#define SCREEN_W        LCD_WIDTH     // 368
#define SCREEN_H        LCD_HEIGHT    // 448
#define CX              (SCREEN_W / 2) // 184
#define CY              (SCREEN_H / 2) // 224

// ===================== Colour Palette (RGB565) =====================
#define COL_BG          0x0000        // Pure black (AMOLED pixels off)
#define COL_ARROW       0x07E8        // Fixed accent green (UI / status / media screens)
#define COL_DIST        0xFFFF        // Fixed white (UI / status / media screens)
#define COL_STREET      0xC618        // Fixed light grey (UI / status / media screens)
#define COL_DIM         0x4208        // Dark grey
#define COL_RED         0xF800        // Red (disconnected / low battery)

// Gauge & Navigation UI additions (matching Figma design)
#define COL_GAUGE_BG    0x0186        // Inactive circular arc track (#00EEFF at 20% opacity)
#define COL_GAUGE_FG    0x077F        // Active circular distance bar (#00EEFF electric cyan)
#define COL_DASH        0x2965        // Dashed road stem blocks (#FFFFFF at 15% opacity)
#define COL_DIM_WHITE   0xAD55        // Dim white for street & distance unit (Figma 50% opacity)

// Configurable colors exclusively for active Navigation screen (screen_nav.cpp)
extern uint16_t g_colNavArrow;
extern uint16_t g_colNavDist;
extern uint8_t  g_cpuMhz;
extern bool     g_pollHigh;

// ===================== Icon Bitmap Constants =====================
#define ICON_W          40
#define ICON_H          40
#define OUT_ICON_W      200
#define OUT_ICON_H      200

// ===================== Screen Identifiers =====================
enum ScreenId : uint8_t {
  SCR_MAIN  = 0,  // Turn-by-turn navigation & waiting screen
  SCR_MEDIA = 1,  // Music playback control screen
  SCR_INFO  = 2,  // Battery, BLE, and hardware status screen
  SCR_COUNT = 3
};

// ===================== Shared Application State =====================
extern Arduino_GFX    *gfx;
extern Arduino_Canvas *g_canvas;

extern bool     g_connected;
extern uint8_t  g_screen;

// Navigation state
extern String   g_maneuver;
extern String   g_distance;
extern String   g_street;
extern float    g_navProgress;   // 0.0f .. 1.0f (0-100% distance to maneuver)
extern int      g_maxLegMeters;  // Max observed meters for current maneuver
extern bool     g_navEnded;
extern bool     g_navShown;
extern uint8_t  g_iconBits[ICON_W * ICON_H / 8];
extern bool     g_iconValid;
extern uint16_t *g_iconCanvas;

// Media state
extern uint8_t  g_mediaState;   // 0 = stopped, 1 = paused, 2 = playing
extern String   g_mediaTitle;
extern String   g_mediaArtist;

// Battery & IMU state
extern int      g_batLevel;     // -1 = unknown, 0..100 = percentage
extern bool     g_batCharge;
extern bool     g_hasBatteryIC;
extern bool     g_hasImu;
extern bool     g_imuMoving;

// Dirty flags for partial/full screen redraws
extern bool     g_dirtyAll;
extern bool     g_dirtyIcon;
extern bool     g_dirtyDist;
extern bool     g_dirtyStreet;
extern bool     g_dirtyMedia;

// ===================== Shared UI Helpers =====================
void   flushDisplay();
void   drawTextC(const String &s, int16_t centerY, const GFXfont *font, uint16_t color);
String fitStreet(String s, const GFXfont *font, uint16_t maxW);
void   sendMediaCommand(char cmd);
