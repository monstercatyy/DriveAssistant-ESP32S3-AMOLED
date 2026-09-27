#pragma once

#include "Arduino_GFX.h"
#include "Arduino_TFT.h"
#include "Arduino_OLED.h"

#define SH8601_TFTWIDTH 480  ///< SH8601 max TFT width
#define SH8601_TFTHEIGHT 480 ///< SH8601 max TFT height

#define SH8601_RST_DELAY 200    ///< delay ms wait for reset finish
#define SH8601_SLPIN_DELAY 120  ///< delay ms wait for sleep in finish
#define SH8601_SLPOUT_DELAY 120 ///< delay ms wait for sleep out finish

// User Command
#define SH8601_C_NOP 0x00
#define SH8601_C_SWRESET 0x01
#define SH8601_C_SLPIN 0x10
#define SH8601_C_SLPOUT 0x11
#define SH8601_C_PTLON 0x12
#define SH8601_C_NORON 0x13
#define SH8601_C_INVOFF 0x20
#define SH8601_C_INVON 0x21
#define SH8601_C_ALLPOFF 0x22
#define SH8601_C_ALLPON 0x23
#define SH8601_C_DISPOFF 0x28
#define SH8601_C_DISPON 0x29
#define SH8601_W_CASET 0x2A
#define SH8601_W_PASET 0x2B
#define SH8601_W_RAMWR 0x2C
#define SH8601_C_TEAROFF 0x34
#define SH8601_WC_TEARON 0x35
#define SH8601_W_MADCTL 0x36
#define SH8601_C_IDLEOFF 0x38
#define SH8601_C_IDLEON 0x39
#define SH8601_W_PIXFMT 0x3A
#define SH8601_W_WRMC 0x3C
#define SH8601_W_WDBRIGHTNESSVALNOR 0x51
#define SH8601_W_WCTRLD1 0x53
#define SH8601_W_WCE 0x58
#define SH8601_W_WDBRIGHTNESSVALHBM 0x63

#define SH8601_MADCTL_X_AXIS_FLIP 0x02
#define SH8601_MADCTL_Y_AXIS_FLIP 0x05
#define SH8601_MADCTL_RGB 0x00
#define SH8601_MADCTL_BGR 0x08
#define SH8601_MADCTL_COLOR_ORDER SH8601_MADCTL_RGB

enum
{
  SH8601_ContrastOff = 0,
  SH8601_LowContrast,
  SH8601_MediumContrast,
  SH8601_HighContrast
};

static const uint8_t sh8601_init_operations[] = {
    BEGIN_WRITE,
    WRITE_COMMAND_8, SH8601_C_SLPOUT,
    END_WRITE,
    DELAY, SH8601_SLPOUT_DELAY,
    BEGIN_WRITE,
    WRITE_COMMAND_8, SH8601_C_NORON,
    WRITE_COMMAND_8, SH8601_C_INVOFF,
    WRITE_C8_D8, SH8601_W_PIXFMT, 0x05, // Interface Pixel Format 16bit/pixel
    WRITE_COMMAND_8, SH8601_C_DISPON,
    WRITE_C8_D8, SH8601_W_WCTRLD1, 0x28,
    WRITE_C8_D8, SH8601_W_WDBRIGHTNESSVALNOR, 0xD0,
    WRITE_C8_D8, SH8601_W_WCE, 0x00,
    END_WRITE,
    DELAY, 10};

class Arduino_SH8601 : public Arduino_OLED
{
public:
  Arduino_SH8601(
      Arduino_DataBus *bus, int8_t rst = GFX_NOT_DEFINED, uint8_t r = 0,
      int16_t w = SH8601_TFTWIDTH, int16_t h = SH8601_TFTHEIGHT,
      uint8_t col_offset1 = 0, uint8_t row_offset1 = 0, uint8_t col_offset2 = 0, uint8_t row_offset2 = 0);

  bool begin(int32_t speed = GFX_NOT_DEFINED) override;
  void writeAddrWindow(int16_t x, int16_t y, uint16_t w, uint16_t h) override;
  void setRotation(uint8_t r) override;
  void invertDisplay(bool) override;
  void displayOn() override;
  void displayOff() override;

  void setBrightness(uint8_t brightness) override;
  void setContrast(uint8_t contrast) override;
  void Display_Brightness(uint8_t brightness) { setBrightness(brightness); }

protected:
  void tftInit() override;
};
