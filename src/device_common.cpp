/*
 * device_common.cpp — AXP2101 PMU power management & RGB565 blending
 */

#include <Arduino.h>
#include "device_common.h"

AppConfig g_config = { 200, 600 };   // Default brightness 200, 10-minute idle timeout

static unsigned long s_last_act_ms = 0;
static bool          s_pwr_short_pending = false;
static bool          s_pwr_long_pending  = false;

void common_init()
{
    power.enableBattDetection();
    power.enableBattVoltageMeasure();

    // Configure physical PWR button:
    //  - Power-ON press threshold: 1 Second (avoids auto-turn-on upon releasing long-press)
    //  - Power-OFF hardware threshold: 4 Seconds
    //  - Long-press IRQ time: 1.5 Seconds
    //  - Set hardware long press behavior to POWER OFF (not restart!)
    power.setPowerKeyPressOnTime(XPOWERS_POWERON_1S);
    power.setPowerKeyPressOffTime(XPOWERS_POWEROFF_4S);
    power.setIrqLevelTime(XPOWERS_AXP2101_IRQ_TIME_1S5);
    power.setLongPressPowerOFF();
    power.enableLongPressShutdown();
    power.disableIRQ(XPOWERS_AXP2101_ALL_IRQ);
    power.clearIrqStatus();
    power.enableIRQ(XPOWERS_AXP2101_PKEY_LONG_IRQ |
                    XPOWERS_AXP2101_PKEY_SHORT_IRQ);

    s_pwr_short_pending = false;
    s_pwr_long_pending  = false;
    s_last_act_ms = millis();
}

void common_activity()
{
    s_last_act_ms = millis();
}

void common_tick()
{
    power.getIrqStatus();
    bool pwr_long  = power.isPekeyLongPressIrq();
    bool pwr_short = power.isPekeyShortPressIrq();
    if (pwr_long || pwr_short) {
        power.clearIrqStatus();
    }
    if (pwr_short && !pwr_long) {
        s_pwr_short_pending = true;
    }

    // Long-press PWR (>1.5 s) -> latch event so main loop can play shutdown animation
    if (pwr_long) {
        s_pwr_long_pending = true;
    }

    // Optional idle timeout shutdown
    if (g_config.timeout_s > 0 &&
        (millis() - s_last_act_ms >= g_config.timeout_s * 1000UL)) {
        s_pwr_long_pending = true;
    }
}

bool common_consume_pwr_short()
{
    bool v = s_pwr_short_pending;
    s_pwr_short_pending = false;
    return v;
}

bool common_consume_pwr_long()
{
    bool v = s_pwr_long_pending;
    s_pwr_long_pending = false;
    return v;
}

uint16_t blend565(uint16_t fg, uint16_t bg, uint8_t alpha)
{
    if (alpha == 0)   return bg;
    if (alpha == 255) return fg;
    uint32_t a   = alpha;
    uint32_t ia  = 255 - a;
    uint32_t r   = (((fg >> 11) & 0x1F) * a + ((bg >> 11) & 0x1F) * ia) / 255;
    uint32_t g   = (((fg >> 5)  & 0x3F) * a + ((bg >> 5)  & 0x3F) * ia) / 255;
    uint32_t b   = (( fg        & 0x1F) * a + ( bg        & 0x1F) * ia) / 255;
    return (uint16_t)((r << 11) | (g << 5) | b);
}