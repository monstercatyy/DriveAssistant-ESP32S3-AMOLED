/*
 * screen_info.cpp — Device & Battery Status Screen Component (SCR_INFO)
 */

#include "screen_info.h"
#include "../hw_panel.h"

static void drawBatterySymbol(int16_t cx0, int16_t cy0) {
  const int16_t w  = 92, h  = 42;
  const int16_t x0 = cx0 - w / 2, y0 = cy0 - h / 2;

  gfx->drawRoundRect(x0, y0, w, h, 9, COL_STREET);
  gfx->drawRoundRect(x0 + 1, y0 + 1, w - 2, h - 2, 8, COL_STREET);
  gfx->fillRoundRect(x0 + w + 3, cy0 - 7, 5, 14, 2, COL_STREET);

  if (g_batLevel > 0) {
    uint16_t c = (g_batLevel > 50) ? COL_ARROW
               : (g_batLevel > 20) ? 0xFE60 /* yellow */ : COL_RED;
    int16_t fill = (int16_t)((w - 10) * g_batLevel / 100);
    if (fill < 4) fill = 4;
    gfx->fillRoundRect(x0 + 5, y0 + 5, fill, h - 10, 5, c);
  } else if (!g_hasBatteryIC) {
    gfx->fillRect(x0 + 14, cy0 - 1, w - 28, 3, COL_DIM);
  }

  // Charging lightning bolt
  if (g_batCharge) {
    gfx->fillTriangle(cx0 + 7, y0 - 4, cx0 - 9, cy0 + 4, cx0 + 2, cy0 + 4, COL_DIST);
    gfx->fillTriangle(cx0 - 7, y0 + h + 4, cx0 + 9, cy0 - 4, cx0 - 2, cy0 - 4, COL_DIST);
  }
}

void screen_info_draw() {
  gfx->fillScreen(COL_BG);
  drawTextC("Status", 66, &SFCompactBold12pt7b, COL_DIST);

  drawBatterySymbol(CX, 138);
  String bat;
  if (!g_hasBatteryIC)      bat = "Battery n/a (USB)";
  else if (g_batLevel < 0)  bat = "Battery unknown";
  else {
    bat = "Battery " + String(g_batLevel) + "%";
    if (g_batCharge) bat += " - charging";
  }
  drawTextC(bat, 198, &SFCompactBold9pt7b, COL_STREET);

  drawTextC(g_connected ? "BLE Connected" : "BLE Disconnected",
            240, &SFCompactBold12pt7b, g_connected ? COL_ARROW : COL_RED);

  String imuStr = !g_hasImu ? "IMU: not detected"
                            : (g_imuMoving ? "IMU: QMI8658 (Moving)"
                                           : "IMU: QMI8658 (Stationary)");
  drawTextC(imuStr, 285, &SFCompactBold9pt7b,
            (g_hasImu && g_imuMoving) ? COL_ARROW : COL_STREET);

  drawTextC(hw_is_v2() ? "HW: V2 (CO5300 + CST816)" : "HW: V1 (SH8601 + FT3168)",
            320, &SFCompactBold9pt7b, COL_DIM);

  char cpuBuf[40];
  snprintf(cpuBuf, sizeof(cpuBuf), "CPU: %uMHz | Poll: %s", g_cpuMhz, g_pollHigh ? "HIGH" : "LOW");
  drawTextC(cpuBuf, 355, &SFCompactBold9pt7b, COL_DIM);

  drawTextC(String("Firmware v") + FW_VERSION, 390, &SFCompactBold9pt7b, COL_DIM);
}
