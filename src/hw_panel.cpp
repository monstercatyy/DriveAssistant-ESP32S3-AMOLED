/*
 * hw_panel.cpp — see hw_panel.h. Auto-detects V1 (SH8601/FT3168) vs
 * V2 (CO5300/CST816) and builds the matching display + touch drivers.
 */
#include "hw_panel.h"
#include "pin_config.h"
#include <Wire.h>
#include "TouchDrv.hpp"

// Probe I2C 0x15 once: CST816 (V2) ACKs there; nothing on V1 does.
bool hw_is_v2() {
    static int cached = -1;            // -1 unknown, 0 = V1, 1 = V2
    if (cached < 0) {
        Wire.beginTransmission(CST816_ADDR);  // 0x15
        cached = (Wire.endTransmission() == 0) ? 1 : 0;
    }
    return cached == 1;
}

Arduino_OLED *make_display(Arduino_DataBus *bus) {
    if (hw_is_v2()) {
        return new Arduino_CO5300(bus, GFX_NOT_DEFINED, 0, LCD_WIDTH, LCD_HEIGHT,
                                  PANEL_CO5300_XOFF, 0,
                                  480 - LCD_WIDTH - PANEL_CO5300_XOFF, 480 - LCD_HEIGHT);
    }
    return new Arduino_SH8601(bus, GFX_NOT_DEFINED, 0, LCD_WIDTH, LCD_HEIGHT);
}

TouchDrvInterface *make_touch() {
    if (hw_is_v2()) {
        TouchDrvCST816 *t = new TouchDrvCST816();
        t->begin(Wire, CST816_ADDR, IIC_SDA, IIC_SCL);
        t->disableAutoSleep();
        return t;
    }
    TouchDrvFT6X36 *t = new TouchDrvFT6X36();
    t->begin(Wire, FT3168_ADDR, IIC_SDA, IIC_SCL);
    return t;
}