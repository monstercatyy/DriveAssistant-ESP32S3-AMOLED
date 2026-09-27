#pragma once

#define XPOWERS_CHIP_AXP2101

// Waveshare ESP32-S3 1.8" AMOLED pin map.
// These are the board-level definitions used by the display, touch controller,
// audio codec, and SD card; the firmware aliases below keep the rest of the
// code independent from the exact hardware variant.

// Display controller bus (board-specific GPIOs for the Waveshare
// ESP32-S3 Touch AMOLED 1.8 board)
#define LCD_SDIO0 4
#define LCD_SDIO1 5
#define LCD_SDIO2 6
#define LCD_SDIO3 7
#define LCD_SCLK 11
#define LCD_CS 12
#define LCD_WIDTH 368
#define LCD_HEIGHT 448

// Aliases expected by Arduino_GFX and the rest of the firmware.
#define TFT_SCK LCD_SCLK
#define TFT_MOSI LCD_SDIO0
#define TFT_CS LCD_CS
#define TFT_DC 2
#define TFT_RST -1
#define TFT_BL 3

// Touch controller (FT3168 / FocalTech family)
#define IIC_SDA 15
#define IIC_SCL 14
#define TP_INT 21
#define TOUCH_SDA IIC_SDA
#define TOUCH_SCL IIC_SCL
#define TOUCH_RST 1
#define TOUCH_INT TP_INT
#define FT3168_ADDR 0x38
#define CST816_ADDR 0x15

// Audio / codec
#define I2S_MCK_IO 16
#define I2S_BCK_IO 9
#define I2S_DI_IO 10
#define I2S_WS_IO 45
#define I2S_DO_IO 8

#define MCLKPIN 16
#define BCLKPIN 9
#define WSPIN 45
#define DOPIN 10
#define DIPIN 8
#define PA 46

// SD
const int SDMMC_CLK = 2;
const int SDMMC_CMD = 1;
const int SDMMC_DATA = 3;