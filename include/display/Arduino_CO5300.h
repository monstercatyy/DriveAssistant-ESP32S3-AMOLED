#pragma once

#include "Arduino_GFX.h"
#include "Arduino_TFT.h"
#include "Arduino_OLED.h"

#define CO5300_TFTWIDTH 480  ///< CO5300 max TFT width
#define CO5300_TFTHEIGHT 480 ///< CO5300 max TFT height

#define CO5300_RST_DELAY 200    ///< delay ms wait for reset finish
#define CO5300_SLPIN_DELAY 120  ///< delay ms wait for sleep in finish
#define CO5300_SLPOUT_DELAY 120 ///< delay ms wait for sleep out finish

#define CO5300_C_NOP 0x00
#define CO5300_C_SWRESET 0x01
#define CO5300_C_SLPIN 0x10
#define CO5300_C_SLPOUT 0x11
#define CO5300_C_PTLON 0x12
#define CO5300_C_NORON 0x13
#define CO5300_C_INVOFF 0x20
#define CO5300_C_INVON 0x21
#define CO5300_C_ALLPOFF 0x22
#define CO5300_C_ALLPON 0x23
#define CO5300_C_DISPOFF 0x28
#define CO5300_C_DISPON 0x29
#define CO5300_W_CASET 0x2A
#define CO5300_W_PASET 0x2B
#define CO5300_W_RAMWR 0x2C
#define CO5300_C_TEAROFF 0x34
#define CO5300_WC_TEARON 0x35
#define CO5300_W_MADCTL 0x36
#define CO5300_C_IDLEOFF 0x38
#define CO5300_C_IDLEON 0x39
#define CO5300_W_PIXFMT 0x3A
#define CO5300_W_WRMC 0x3C
#define CO5300_W_DEEPSTMODE 0x4F
#define CO5300_W_WDBRIGHTNESSVALNOR 0x51
#define CO5300_W_WCTRLD1 0x53
#define CO5300_W_WCE 0x58
#define CO5300_W_WDBRIGHTNESSVALHBM 0x63
#define CO5300_W_SPIMODECTL 0xC4

#define CO5300_MADCTL_X_AXIS_FLIP 0x02
#define CO5300_MADCTL_Y_AXIS_FLIP 0x05
#define CO5300_MADCTL_RGB 0x00
#define CO5300_MADCTL_BGR 0x08
#define CO5300_MADCTL_COLOR_ORDER CO5300_MADCTL_RGB

enum
{
  CO5300_ContrastOff = 0,
  CO5300_LowContrast,
  CO5300_MediumContrast,
  CO5300_HighContrast
};

static const uint8_t co5300_init_operations[] = {
    BEGIN_WRITE,
    WRITE_COMMAND_8, CO5300_C_SLPOUT,
    END_WRITE,
    DELAY, CO5300_SLPOUT_DELAY,
    BEGIN_WRITE,
    WRITE_C8_D8, 0xFE, 0x00,
    WRITE_C8_D8, CO5300_W_SPIMODECTL, 0x80,
    WRITE_C8_D8, CO5300_W_PIXFMT, 0x55, // Interface Pixel Format 16bit/pixel
    WRITE_C8_D8, CO5300_W_WCTRLD1, 0x20,
    WRITE_C8_D8, CO5300_W_WDBRIGHTNESSVALHBM, 0xFF,
    WRITE_COMMAND_8, CO5300_C_DISPON,
    WRITE_C8_D8, CO5300_W_WDBRIGHTNESSVALNOR, 0xD0,
    WRITE_C8_D8, CO5300_W_WCE, 0x00,
    END_WRITE,
    DELAY, 10};

class Arduino_CO5300 : public Arduino_OLED
{
public:
  Arduino_CO5300(
      Arduino_DataBus *bus, int8_t rst = GFX_NOT_DEFINED, uint8_t r = 0,
      int16_t w = CO5300_TFTWIDTH, int16_t h = CO5300_TFTHEIGHT,
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
