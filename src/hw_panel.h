/*
 * hw_panel.h — Waveshare ESP32-S3-Touch-AMOLED-1.8 board-revision support.
 *
 * V1: SH8601 display + FT3168 touch (FocalTech @0x38).
 * V2: CO5300 display + CST816  touch (@0x15).
 *
 * Detected at boot by probing I2C 0x15 (only V2's CST816 answers there). V1 is
 * the default/fallback, so a V1 device always uses the original, proven path.
 * Call make_display()/make_touch() from the sketch's setup() AFTER Wire.begin().
 */
#pragma once
#include "Arduino_GFX_Library.h"
#include "Arduino_OLED.h"
#include "display/Arduino_SH8601.h"
#include "display/Arduino_CO5300.h"
#include "TouchDrv.hpp"

#define PANEL_CO5300_XOFF 16         // CO5300 GRAM column where the 368px panel starts

bool               hw_is_v2();
Arduino_OLED      *make_display(Arduino_DataBus *bus);
TouchDrvInterface *make_touch();