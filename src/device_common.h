/*
 * device_common.h — AXP2101 PMU power management & colour blending helpers
 */

#pragma once

#include <stdint.h>
#include "pin_config.h"          // Must precede XPowersLib — defines XPOWERS_CHIP_AXP2101
#include "XPowersLib.h"
#include "Arduino_GFX_Library.h"
#include "canvas/Arduino_Canvas.h"

// Shared configuration
struct AppConfig {
    uint16_t brightness;   // Display brightness (0–255)
    uint32_t timeout_s;    // Idle seconds before auto power-off (0 = disabled)
};

extern AppConfig       g_config;
extern XPowersPMU      power;
extern Arduino_Canvas *g_canvas;

// Power & button lifecycle
void common_init();
void common_activity();
void common_tick();

// Consumes a single physical PWR button short-press event latched by common_tick()
bool common_consume_pwr_short();

// Alpha-blends foreground RGB565 colour `fg` over background `bg` (alpha: 0..255)
uint16_t blend565(uint16_t fg, uint16_t bg, uint8_t alpha);