/*
  Drive Assistant — Firmware for Waveshare ESP32-S3-Touch-AMOLED-1.8
  ==================================================================
  Hardware Support (auto-detected at runtime via I2C @ 0x15):
  - V1 Board: SH8601 AMOLED (368x448 QSPI) + FT3168 Touch (@0x38) + XCA9554 (@0x20)
  - V2 Board: CO5300 AMOLED (368x448 QSPI, col_offset1=16) + CST816 Touch (@0x15)
  - Both Boards: AXP2101 PMU (@0x34) + QMI8658 6-Axis IMU (@0x6B)

  Screen Components (in src/screens/):
  - Screen 0 (SCR_MAIN):  Navigation & Waiting Screen (screen_nav.cpp)
  - Screen 1 (SCR_MEDIA): Music Playback Control Screen (screen_media.cpp)
  - Screen 2 (SCR_INFO):  Device, IMU & Battery Status Screen (screen_info.cpp)
*/

#include <Arduino.h>
#include <Wire.h>
#include <NimBLEDevice.h>
#include <esp_sleep.h>

#include "pin_config.h"
#include "device_common.h"
#include "hw_panel.h"

#include "screens/ui_common.h"
#include "screens/screen_nav.h"
#include "screens/screen_media.h"
#include "screens/screen_info.h"

// ===================== BLE UUIDs =====================
#define DEVICE_NAME      "Drive Assistant"
#define SERVICE_UUID     "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
#define NAV_CHAR_UUID    "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
#define ICON_CHAR_UUID   "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
#define MEDIA_CHAR_UUID  "6e400004-b5a3-f393-e0a9-e50e24dcca9e"
#define CMD_CHAR_UUID    "6e400005-b5a3-f393-e0a9-e50e24dcca9e"

// ===================== Touch & Power Constants =====================
#define XCA9554_ADDR     0x20
#define SWIPE_MIN_PX     40
#define LONGPRESS_MS     600
#define SLEEP_TIMEOUT_MS (10UL * 60UL * 1000UL)   // 10 min without BLE/motion/touch -> power off
#define NAV_END_SHOW_MS  3500UL                   // Show "Navigation ended" for 3.5 s

enum TouchEvent : uint8_t {
  TE_NONE = 0,
  TE_TAP,
  TE_LONG_PRESS,
  TE_SWIPE_LEFT,
  TE_SWIPE_RIGHT,
  TE_SWIPE_UP,
  TE_SWIPE_DOWN
};

// ===================== Hardware & Runtime Globals =====================
XPowersPMU power;

static Arduino_DataBus *bus = new Arduino_ESP32QSPI(
    LCD_CS, LCD_SCLK, LCD_SDIO0, LCD_SDIO1, LCD_SDIO2, LCD_SDIO3);

static Arduino_OLED         *g_panel          = nullptr;
static TouchDrvInterface    *g_touch          = nullptr;
static NimBLECharacteristic *g_cmdChar        = nullptr;
static bool                  g_pmuOk          = false;
static bool                  g_backlight      = true;
static uint32_t              g_lastActivity   = 0;
static uint32_t              g_navEndedAt     = 0;
static String                g_lastPayload    = "";
static int                   g_prevDistMeters = -1;
static uint32_t              g_prevDistTimeMs = 0;
static uint32_t              g_msPer10m       = 1000; // Pace per 10 m (default 1.0 s = 36 km/h)
static bool                  g_sub50Active    = false;
static bool                  g_sub50Done      = false;
static int                   g_sub50Meters    = 40;
static uint32_t              g_sub50StepAt    = 0;
static uint32_t              g_sub50Hold10At  = 0;
static int16_t               g_tapX = 0, g_tapY = 0;

// ===================== Screen Dispatcher =====================
void redraw() {
  if (g_screen == SCR_INFO) {
    if (g_dirtyAll) screen_info_draw();
    g_dirtyAll = g_dirtyIcon = g_dirtyDist = g_dirtyStreet = g_dirtyMedia = false;
    g_navShown = false;
    flushDisplay();
    return;
  }

  if (g_screen == SCR_MEDIA) {
    if (g_dirtyAll || g_dirtyMedia) screen_media_draw();
    g_dirtyAll = g_dirtyIcon = g_dirtyDist = g_dirtyStreet = g_dirtyMedia = false;
    g_navShown = false;
    flushDisplay();
    return;
  }

  g_dirtyMedia = false;
  screen_nav_draw();
  g_dirtyAll = g_dirtyIcon = g_dirtyDist = g_dirtyStreet = false;
  flushDisplay();
}

void sendMediaCommand(char cmd) {
  if (g_cmdChar && g_connected) {
    g_cmdChar->setValue((uint8_t *)&cmd, 1);
    g_cmdChar->notify();
    Serial.printf("[MEDIA] command '%c'\n", cmd);
  }
}

// ===================== Navigation Payload Parsing =====================
static int parseDistanceMeters(const String &d) {
  if (d.length() == 0) return -1;
  String low = d;
  low.toLowerCase();
  low.replace(",", ".");
  float val = low.toFloat();
  if (val <= 0.0f && low[0] != '0') return -1;
  if (low.indexOf("km") >= 0) return (int)(val * 1000.0f);
  if (low.indexOf("mi") >= 0) return (int)(val * 1609.34f);
  if (low.indexOf("ft") >= 0) return (int)(val * 0.3048f);
  if (low.indexOf("yd") >= 0) return (int)(val * 0.9144f);
  return (int)val;
}

static String inferManeuverFromText(const String &text) {
  String t = text;
  t.toLowerCase();
  t.trim();
  // MUST check destination/arrival FIRST so "Destination will be on the right/left"
  // (or NavParser's extracted "the right" / "the left" after splitting on " on ")
  // is not misclassified as a right/left turn!
  if (t.indexOf("destination") >= 0 || t.indexOf("you have arrived") >= 0 ||
      t.indexOf("you've arrived") >= 0 || t.indexOf("arrived") >= 0 ||
      t.indexOf("ziel") >= 0 || t.indexOf("angekommen") >= 0 ||
      t == "the right" || t == "the left" ||
      t.startsWith("the right ") || t.startsWith("the left "))
    return "arrive";
  if (t.indexOf("roundabout") >= 0 || t.indexOf("kreisverkehr") >= 0 || t.indexOf("exit") >= 0)
    return "roundabout";
  if (t.indexOf("u-turn") >= 0 || t.indexOf("uturn") >= 0 || t.indexOf("wenden") >= 0)
    return "uturn";
  if (t.indexOf("sharp right") >= 0 || t.indexOf("scharf rechts") >= 0)
    return "sharp-right";
  if (t.indexOf("sharp left") >= 0 || t.indexOf("scharf links") >= 0)
    return "sharp-left";
  if (t.indexOf("slight right") >= 0 || t.indexOf("keep right") >= 0 || t.indexOf("halb rechts") >= 0)
    return "slight-right";
  if (t.indexOf("slight left") >= 0 || t.indexOf("keep left") >= 0 || t.indexOf("halb links") >= 0)
    return "slight-left";
  if (t.indexOf("right") >= 0 || t.indexOf("rechts") >= 0)
    return "right";
  if (t.indexOf("left") >= 0 || t.indexOf("links") >= 0)
    return "left";
  if (t.indexOf("merge") >= 0 || t.indexOf("einordnen") >= 0)
    return "merge";
  return "straight";
}

void applyPayload(const String &rawPayload) {
  String payload = rawPayload;
  bool isTestPacket = (payload == "turn-right|200 m|Teststrasse" ||
                       payload.endsWith("|Teststrasse"));

  if (isTestPacket) {
    static const char *const kManeuvers[] = {
      "turn-right",   "turn-left",    "slight-right", "slight-left",
      "sharp-right",  "sharp-left",   "uturn",        "roundabout",
      "merge",        "straight",     "arrive"
    };
    static const char *const kLabels[] = {
      "Turn Right",   "Turn Left",    "Slight Right", "Slight Left",
      "Sharp Right",  "Sharp Left",   "U-Turn",       "Roundabout",
      "Merge",        "Straight",     "Destination"
    };
    static uint8_t s_testIdx = 0;
    const uint8_t idx = s_testIdx;
    s_testIdx = (s_testIdx + 1) % 11;

    // Randomize distance between 10 m and 5000 m (5.0 km):
    // Half the time pick 10 m .. 990 m (in 10 m steps) so < 50 m and meter steps are easy to see,
    // half the time pick 1.0 km .. 5.0 km (in 0.1 km / 100 m steps).
    uint32_t r = esp_random();
    String distStr;
    if (r & 1) {
      int meters = (int)((r >> 1) % 99) * 10 + 10; // 10 m .. 990 m
      distStr = String(meters) + " m";
    } else {
      int tenthsKm = (int)((r >> 1) % 41) + 10;    // 10 .. 50 -> 1.0 km .. 5.0 km
      distStr = String(tenthsKm / 10) + "." + String(tenthsKm % 10) + " km";
    }

    g_sub50Active   = false;
    g_sub50Done     = false;
    g_sub50Hold10At = 0;
    payload = String(kManeuvers[idx]) + "|" + distStr + "|" + String(kLabels[idx]);
  } else {
    if (payload == g_lastPayload) return;
  }
  g_lastPayload = payload;

  int p1 = payload.indexOf('|');
  int p2 = (p1 >= 0) ? payload.indexOf('|', p1 + 1) : -1;

  String m, d, s;
  if (p1 >= 0) {
    m = payload.substring(0, p1);
    d = (p2 >= 0) ? payload.substring(p1 + 1, p2) : payload.substring(p1 + 1);
    s = (p2 >= 0) ? payload.substring(p2 + 1) : "";
  }
  m.trim(); d.trim(); s.trim();
  if (m == "arrived") m = "arrive";

  // Strip any accidental ETA suffix (e.g. "Arrive 11:05 PM") from street string
  int etaIdx = s.indexOf(" Arrive ");
  if (etaIdx > 0) {
    s = s.substring(0, etaIdx);
    s.trim();
  }

  int curMeters = (d == "< 50 m") ? -1 : parseDistanceMeters(d);

  // Clean up street string if NavParser.extractStreet split "Destination will be on the right/left"
  // at " on ", or extracted "at <Destination>" from "You've arrived at <Destination>"
  String sLow = s;
  sLow.toLowerCase();
  bool isAtDestination = false;
  if (sLow == "the right" || sLow.startsWith("the right ")) {
    m = "arrive";
    s = "Destination on the right";
    isAtDestination = true;
  } else if (sLow == "the left" || sLow.startsWith("the left ")) {
    m = "arrive";
    s = "Destination on the left";
    isAtDestination = true;
  } else if (sLow.startsWith("at ") && s.length() > 3) {
    m = "arrive";
    s = s.substring(3);
    s.trim();
    isAtDestination = true;
  }

  // If the street/notification text mentions destination/arrival, always set m = "arrive".
  String inferredFromStreet = inferManeuverFromText(s);
  if (inferredFromStreet == "arrive") {
    m = "arrive";
    isAtDestination = true;
  }

  // Distinguish stopped in traffic at an intermediate turn vs stopped at arrival:
  // If we are on an active directional maneuver (turn-right, turn-left, straight, etc.)
  // for this street and the street is NOT a destination card, keep the turn maneuver even at 0 m!
  if (m == "arrive" && !isTestPacket && !isAtDestination) {
    if (inferredFromStreet != "straight") {
      m = inferredFromStreet;
    } else if (s == g_street && g_maneuver.length() > 0 &&
               g_maneuver != "arrive" && g_maneuver != "clear" && g_maneuver != "end") {
      m = g_maneuver;
    } else if (curMeters > 60) {
      m = "straight";
    }
  }

  // When Google Maps shows the final destination/arrival card (e.g. "at Bogo Cemetery", "Arrived",
  // or the destination place name with no direction keyword), NavParser sends m = "unknown".
  if (m == "unknown") {
    if (isAtDestination || (s.length() > 0 && (d == "< 50 m" || d.length() == 0 || curMeters == 0))) {
      m = "arrive";
      isAtDestination = true;
    } else {
      m = "straight";
    }
  }

  g_sub50Active = false;
  g_sub50Done   = false;

  // Only show "Arrived" when m == "arrive" (actual destination) and distance is 0 m, empty, or "< 50 m".
  // When stopped in traffic at a turn (m != "arrive"), 0 m stays d = "0 m" with the turn icon!
  if (m == "arrive" && !isTestPacket &&
      (isAtDestination || d.length() == 0 || d == "0 m" || curMeters == 0 || d == "< 50 m")) {
    g_prevDistMeters = -1;
    d = "Arrived";
    if (s.length() == 0) s = "Destination";
    payload = String("arrived|0 m|") + s;
  } else {
    if (d == "< 50 m") {
      d = "40 m";
      curMeters = 40;
      payload = m + "|40 m|" + s;
    } else if (curMeters == 0 && m != "arrive") {
      d = "0 m";
      payload = m + "|0 m|" + s;
    }
    if (curMeters >= 0) {
      g_prevDistMeters = curMeters;
      g_prevDistTimeMs = millis();
    }
  }

  bool wasNav = g_maneuver.length() > 0 &&
                g_maneuver != "clear" && g_maneuver != "end";
  bool isNav  = m.length() > 0 && m != "clear" && m != "end";

  if (m == "end" && wasNav) {
    g_navEnded       = true;
    g_navEndedAt     = millis();
    g_prevDistMeters = -1;
    g_sub50Active    = false;
    g_sub50Done      = false;
    g_sub50Hold10At  = 0;
  }
  if (isNav) g_navEnded = false;

  if (m != g_maneuver) g_dirtyIcon   = true;
  if (d != g_distance) g_dirtyDist   = true;
  if (s != g_street)   g_dirtyStreet = true;
  g_maneuver = m; g_distance = d; g_street = s;

  if (wasNav != isNav) g_dirtyAll = true;
  if (!isNav) {
    g_iconValid      = false;
    g_prevDistMeters = -1;
    g_sub50Active    = false;
    g_sub50Done      = false;
    g_sub50Hold10At  = 0;
  }

  Serial.printf("[NAV] raw='%s' -> m='%s' d='%s' s='%s'\n",
                payload.c_str(), g_maneuver.c_str(), g_distance.c_str(), g_street.c_str());
}

// ===================== BLE GATT Callbacks =====================
class ServerCallbacks : public NimBLEServerCallbacks {
  void onConnect(NimBLEServer *s, NimBLEConnInfo &info) override {
    g_connected = true;
    g_dirtyAll  = true;
    Serial.println("[BLE] client connected");
  }
  void onDisconnect(NimBLEServer *s, NimBLEConnInfo &info, int reason) override {
    g_connected      = false;
    g_dirtyAll       = true;
    g_lastPayload    = "";
    g_maneuver       = "";
    g_iconValid      = false;
    g_prevDistMeters = -1;
    g_sub50Active    = false;
    Serial.println("[BLE] client disconnected -> restarting advertising");
    NimBLEDevice::startAdvertising();
  }
};

class NavCharCallbacks : public NimBLECharacteristicCallbacks {
  void onWrite(NimBLECharacteristic *c, NimBLEConnInfo &info) override {
    applyPayload(String(c->getValue().c_str()));
  }
};

class IconCharCallbacks : public NimBLECharacteristicCallbacks {
  void onWrite(NimBLECharacteristic *c, NimBLEConnInfo &info) override {
    NimBLEAttValue v = c->getValue();
    if (v.size() >= 3 && v[0] == 'I' && v[1] == 0 && v[2] == 0) {
      g_iconValid = false;
      g_dirtyIcon = true;
      Serial.println("[NAV] icon cleared (vector fallback)");
      return;
    }
    if (v.size() >= 3 + sizeof(g_iconBits) &&
        v[0] == 'I' && v[1] == ICON_W && v[2] == ICON_H) {
      memcpy(g_iconBits, v.data() + 3, sizeof(g_iconBits));

      uint16_t setBits = 0;
      for (size_t i = 0; i < sizeof(g_iconBits); i++) {
        uint8_t b = g_iconBits[i];
        while (b) { setBits += (b & 1); b >>= 1; }
      }

      if (setBits > 880 && setBits < 1540) {
        for (size_t i = 0; i < sizeof(g_iconBits); i++) {
          g_iconBits[i] = ~g_iconBits[i];
        }
        setBits = 1600 - setBits;
        Serial.printf("[NAV] icon auto-inverted (fgBits=%u/1600)\n", setBits);
      }

      if (setBits >= 20 && setBits <= 1520) {
        g_iconValid = true;
        g_dirtyIcon = true;
        Serial.printf("[NAV] icon received (fgBits=%u/1600)\n", setBits);
      } else {
        g_iconValid = false;
        g_dirtyIcon = true;
        Serial.printf("[NAV] icon unusable (fgBits=%u/1600) -> using vector arrow\n", setBits);
      }
    } else {
      Serial.printf("[NAV] icon discarded (len=%u)\n", (unsigned)v.size());
    }
  }
};

class MediaCharCallbacks : public NimBLECharacteristicCallbacks {
  void onWrite(NimBLECharacteristic *c, NimBLEConnInfo &info) override {
    String v = String(c->getValue().c_str());
    int p1 = v.indexOf('|');
    int p2 = (p1 >= 0) ? v.indexOf('|', p1 + 1) : -1;
    String st = (p1 >= 0) ? v.substring(0, p1) : v;
    String ti = (p2 >= 0) ? v.substring(p1 + 1, p2) : "";
    String ar = (p2 >= 0) ? v.substring(p2 + 1) : "";
    ti.trim(); ar.trim();

    uint8_t state = (st == "play") ? 2 : (st == "pause") ? 1 : 0;
    if (state != g_mediaState || ti != g_mediaTitle || ar != g_mediaArtist) {
      g_mediaState  = state;
      g_mediaTitle  = ti;
      g_mediaArtist = ar;
      g_dirtyMedia  = true;
      Serial.printf("[MEDIA] state=%u '%s' - '%s'\n",
                    state, ti.c_str(), ar.c_str());
    }
  }
};

// ===================== Hardware Helpers (V1 / V2 / Touch / Battery / Power) =====================
static void xca9554ResetV1() {
  Wire.beginTransmission(XCA9554_ADDR);
  Wire.write(0x03); // Configuration register
  Wire.write(0xFC); // Bits 0,1 outputs (panel & touch reset)
  if (Wire.endTransmission() != 0) return;

  auto writeOut = [](uint8_t val) {
    Wire.beginTransmission(XCA9554_ADDR);
    Wire.write(0x01); // Output port register
    Wire.write(val);
    Wire.endTransmission();
  };
  writeOut(0x00); delay(20);
  writeOut(0x03); delay(120);
}

static TouchEvent touchPoll() {
  static bool     wasTouched = false;
  static bool     longFired  = false;
  static uint32_t pressStart = 0;
  static int16_t  startX = 0, startY = 0, lastX = 0, lastY = 0;

  bool now = false;
  int16_t x = 0, y = 0;

  if (g_touch) {
    const TouchPoints &tp = g_touch->getTouchPoints();
    if (tp.hasPoints()) {
      const auto &pt = tp.getPoint(0);
      now = true;
      x = pt.x;
      y = pt.y;
    }
  }

  TouchEvent ev = TE_NONE;
  if (now && !wasTouched) {
    startX = lastX = x;
    startY = lastY = y;
    pressStart = millis();
    longFired  = false;
  } else if (now) {
    lastX = x;
    lastY = y;
    int16_t dx = lastX - startX, dy = lastY - startY;
    if (!longFired && abs(dx) < SWIPE_MIN_PX && abs(dy) < SWIPE_MIN_PX &&
        millis() - pressStart >= LONGPRESS_MS) {
      ev = TE_LONG_PRESS;
      longFired = true;
    }
  } else if (wasTouched) {
    if (!longFired) {
      int16_t dx = lastX - startX, dy = lastY - startY;
      if (abs(dx) < SWIPE_MIN_PX && abs(dy) < SWIPE_MIN_PX) {
        ev = TE_TAP;
        g_tapX = startX;
        g_tapY = startY;
      } else if (abs(dx) > abs(dy)) {
        ev = (dx > 0) ? TE_SWIPE_RIGHT : TE_SWIPE_LEFT;
      } else {
        ev = (dy > 0) ? TE_SWIPE_DOWN : TE_SWIPE_UP;
      }
    }
  }
  wasTouched = now;
  return ev;
}

static bool batteryPoll() {
  if (!g_pmuOk) return false;
  int  lvl = power.isBatteryConnect() ? power.getBatteryPercent() : -1;
  bool chg = power.isCharging();
  bool changed = (lvl != g_batLevel) || (chg != g_batCharge);
  g_batLevel  = lvl;
  g_batCharge = chg;
  return changed;
}

static void setBacklight(bool on) {
  g_backlight = on;
  if (g_panel) {
    if (on) {
      g_panel->displayOn();
      g_panel->setBrightness(g_config.brightness > 0 ? (uint8_t)g_config.brightness : 200);
    } else {
      g_panel->setBrightness(0);
      g_panel->displayOff();
    }
  }
  Serial.printf("[PWR] AMOLED display %s\n", on ? "ON" : "OFF");
}

static void goToSleep() {
  Serial.println("[PWR] Sleep timeout reached -> powering down");
  setBacklight(false);
  delay(80);
  if (g_pmuOk) {
    power.clearIrqStatus();
    power.shutdown();
  }
  esp_sleep_enable_ext0_wakeup((gpio_num_t)TP_INT, 0);
  esp_deep_sleep_start();
}

// ===================== QMI8658 6-Axis IMU (Motion / Stop Detector) =====================
static uint8_t  g_imuAddr        = 0x6B;
static bool     g_imuSeeded      = false;
// Fast LPF (~0.8 Hz) vs Slow LPF (~0.15 Hz) on signed axes:
// High-frequency symmetric motor/engine idling vibration (15-100 Hz) cancels to ~0
// inside the signed fast LPF BEFORE magnitude is computed, while real vehicle
// acceleration, braking, road waves (0.3-2 Hz), and steering pass through cleanly.
static float    g_fastAx = 0.0f, g_fastAy = 0.0f, g_fastAz = 1.0f;
static float    g_slowAx = 0.0f, g_slowAy = 0.0f, g_slowAz = 1.0f;
static float    g_fastGx = 0.0f, g_fastGy = 0.0f, g_fastGz = 0.0f;
static float    g_slowGx = 0.0f, g_slowGy = 0.0f, g_slowGz = 0.0f;
static float    g_imuEnergy      = 0.0f;
static uint32_t g_imuLastMoveMs  = 0;

static void imuWriteReg(uint8_t reg, uint8_t val) {
  Wire.beginTransmission(g_imuAddr);
  Wire.write(reg);
  Wire.write(val);
  Wire.endTransmission();
}

static bool imuReadRegs(uint8_t reg, uint8_t *buf, size_t len) {
  Wire.beginTransmission(g_imuAddr);
  Wire.write(reg);
  if (Wire.endTransmission(false) != 0) return false;
  if (Wire.requestFrom((int)g_imuAddr, (int)len) != (int)len) return false;
  for (size_t i = 0; i < len; i++) buf[i] = Wire.read();
  return true;
}

static void imuInit() {
  uint8_t who = 0;
  for (uint8_t addr : { (uint8_t)0x6B, (uint8_t)0x6A }) {
    g_imuAddr = addr;
    if (imuReadRegs(0x00, &who, 1) && who == 0x05) {
      g_hasImu = true;
      break;
    }
  }
  if (!g_hasImu) {
    Serial.println("[IMU] QMI8658 not found on I2C");
    return;
  }

  // Configure QMI8658:
  // - CTRL1 (0x02) = 0x40: Address auto-increment + Little-Endian (bit 5 MUST be 0)
  // - CTRL2 (0x03) = 0x15: Accel +-4g, 125Hz ODR
  // - CTRL3 (0x04) = 0x55: Gyro +-512dps, 125Hz ODR
  // - CTRL5 (0x06) = 0x77: Enable deepest hardware LPF (MODE_3 = 2.66% ODR) for Accel & Gyro
  //                        to reject high-frequency engine/motor vibration at the sensor ADC
  // - CTRL7 (0x08) = 0x03: Enable Accel + Gyro
  imuWriteReg(0x02, 0x40);
  imuWriteReg(0x03, 0x15);
  imuWriteReg(0x04, 0x55);
  imuWriteReg(0x06, 0x77);
  imuWriteReg(0x08, 0x03);
  g_imuSeeded     = false;
  g_imuEnergy     = 0.0f;
  g_imuLastMoveMs = 0;
  g_imuMoving     = false;
  Serial.printf("[IMU] QMI8658 6-axis IMU active (@0x%02X)\n", g_imuAddr);
}

static void imuPoll() {
  if (!g_hasImu) return;
  static uint32_t s_lastImuMs = 0;
  uint32_t now = millis();
  if (now - s_lastImuMs < 25) return; // 40 Hz polling
  s_lastImuMs = now;

  uint8_t status0 = 0;
  if (!imuReadRegs(0x2E, &status0, 1) || (status0 & 0x03) == 0) return;

  uint8_t raw[12];
  if (!imuReadRegs(0x35, raw, 12)) return;

  int16_t rax = (int16_t)((uint16_t)raw[0] | ((uint16_t)raw[1] << 8));
  int16_t ray = (int16_t)((uint16_t)raw[2] | ((uint16_t)raw[3] << 8));
  int16_t raz = (int16_t)((uint16_t)raw[4] | ((uint16_t)raw[5] << 8));
  int16_t rgx = (int16_t)((uint16_t)raw[6] | ((uint16_t)raw[7] << 8));
  int16_t rgy = (int16_t)((uint16_t)raw[8] | ((uint16_t)raw[9] << 8));
  int16_t rgz = (int16_t)((uint16_t)raw[10] | ((uint16_t)raw[11] << 8));

  // Convert to g (+-4g scale -> 8192 LSB/g) and deg/s (+-512dps scale -> 64 LSB/dps)
  float ax = rax * (4.0f / 32768.0f);
  float ay = ray * (4.0f / 32768.0f);
  float az = raz * (4.0f / 32768.0f);
  float gx = rgx * (512.0f / 32768.0f);
  float gy = rgy * (512.0f / 32768.0f);
  float gz = rgz * (512.0f / 32768.0f);

  // Seed both fast and slow filters on first valid sample
  if (!g_imuSeeded) {
    g_fastAx = g_slowAx = ax;
    g_fastAy = g_slowAy = ay;
    g_fastAz = g_slowAz = az;
    g_fastGx = g_slowGx = gx;
    g_fastGy = g_slowGy = gy;
    g_fastGz = g_slowGz = gz;
    g_imuSeeded = true;
    return;
  }

  // Update signed fast LPF (alpha=0.14 -> ~1 Hz) and slow baseline LPF (alpha=0.02 -> ~0.13 Hz).
  // Symmetric motor/engine idle vibration (15-100 Hz) averages out to ~0 in g_fastA/G!
  g_fastAx += (ax - g_fastAx) * 0.14f;
  g_fastAy += (ay - g_fastAy) * 0.14f;
  g_fastAz += (az - g_fastAz) * 0.14f;
  g_slowAx += (ax - g_slowAx) * 0.02f;
  g_slowAy += (ay - g_slowAy) * 0.02f;
  g_slowAz += (az - g_slowAz) * 0.02f;

  g_fastGx += (gx - g_fastGx) * 0.14f;
  g_fastGy += (gy - g_fastGy) * 0.14f;
  g_fastGz += (gz - g_fastGz) * 0.14f;
  g_slowGx += (gx - g_slowGx) * 0.02f;
  g_slowGy += (gy - g_slowGy) * 0.02f;
  g_slowGz += (gz - g_slowGz) * 0.02f;

  // Band-pass sustained vehicle motion (0.15 Hz .. 1.5 Hz)
  float dax = g_fastAx - g_slowAx, day = g_fastAy - g_slowAy, daz = g_fastAz - g_slowAz;
  float dgx = g_fastGx - g_slowGx, dgy = g_fastGy - g_slowGy, dgz = g_fastGz - g_slowGz;

  float accelDev = sqrtf(dax * dax + day * day + daz * daz); // sustained linear accel in g
  float gyroDev  = sqrtf(dgx * dgx + dgy * dgy + dgz * dgz); // sustained rotation in deg/s

  // Deadzone rejects motor/engine idle vibration & small bumps (< 0.055g and < 6.5 deg/s)
  float netAccel = (accelDev > 0.055f) ? (accelDev - 0.055f) : 0.0f;
  float netGyro  = (gyroDev  > 6.5f)   ? (gyroDev  - 6.5f)   : 0.0f;

  float instEnergy = netAccel * 14.0f + netGyro * 0.18f;
  g_imuEnergy = g_imuEnergy * 0.82f + instEnergy * 0.18f;

  if (g_imuEnergy > 0.45f) {
    g_imuLastMoveMs = now;
  }

  // Vehicle/board is considered moving if sustained motion occurred within the last 1.5 seconds
  bool movingNow = (g_imuLastMoveMs > 0) && ((now - g_imuLastMoveMs) < 1500);
  if (movingNow != g_imuMoving) {
    g_imuMoving = movingNow;
    if (g_screen == SCR_INFO) g_dirtyAll = true;
    Serial.printf("[IMU] state -> %s (accelDev=%.3fg gyroDev=%.1fdps)\n",
                  g_imuMoving ? "MOVING" : "STATIONARY", accelDev, gyroDev);
  }
}

// ===================== Arduino Setup & Main Loop =====================
void setup() {
  Serial.begin(115200);
  uint32_t t0 = millis();
  while (!Serial && (millis() - t0 < 1500)) {
    delay(10);
  }
  Serial.println("\n[BOOT] Drive Assistant — Waveshare ESP32-S3-Touch-AMOLED-1.8");

  pinMode(0, INPUT_PULLUP);      // BOOT button
  pinMode(TP_INT, INPUT_PULLUP); // Touch interrupt

  Wire.begin(IIC_SDA, IIC_SCL);

  // 1. Initialize AXP2101 PMU & enable display/peripherals power rails
  g_pmuOk = power.begin(Wire, AXP2101_SLAVE_ADDRESS, IIC_SDA, IIC_SCL);
  if (g_pmuOk) {
    power.setALDO1Voltage(1800); power.enableALDO1();
    power.setALDO2Voltage(2800); power.enableALDO2();
    power.setALDO3Voltage(3300); power.enableALDO3();
    power.setALDO4Voltage(3300); power.enableALDO4();
    power.setBLDO1Voltage(1800); power.enableBLDO1();
    power.setBLDO2Voltage(3300); power.enableBLDO2();
    delay(50);
    common_init();
    common_activity();
    g_hasBatteryIC = true;
    Serial.println("[PMU] AXP2101 active");
  } else {
    Serial.println("[PMU] AXP2101 not found on I2C");
  }

  // 2. Detect V1 (SH8601 + FT3168) vs V2 (CO5300 + CST816)
  bool v2 = hw_is_v2();
  Serial.printf("[HW] Panel variant: %s\n",
                v2 ? "V2 (CO5300 + CST816)" : "V1 (SH8601 + FT3168)");
  if (!v2) {
    xca9554ResetV1();
  }

  // 3. Initialize AMOLED display + PSRAM Canvas
  size_t iconBytes = (size_t)OUT_ICON_W * OUT_ICON_H * sizeof(uint16_t);
  g_iconCanvas = (uint16_t *)(psramFound() ? ps_malloc(iconBytes) : malloc(iconBytes));

  g_panel  = make_display(bus);
  g_canvas = new Arduino_Canvas(LCD_WIDTH, LCD_HEIGHT, g_panel, 0, 0, 0);
  gfx      = g_canvas;
  if (!g_canvas->begin()) {
    Serial.println("[GFX] Canvas begin() failed! Using direct panel output");
    gfx = g_panel;
    g_panel->begin();
  } else {
    Serial.println("[GFX] Display & PSRAM canvas initialized (368x448)");
  }

  g_panel->setBrightness(g_config.brightness > 0 ? (uint8_t)g_config.brightness : 200);
  gfx->fillScreen(COL_BG);
  redraw();
  setBacklight(true);

  // 4. Initialize Touch, 6-Axis IMU & Battery
  g_touch = make_touch();
  if (g_touch) {
    Serial.println("[TOUCH] Touch driver ready");
  }
  imuInit();
  batteryPoll();

  // 5. Initialize NimBLE GATT Server
  NimBLEDevice::init(DEVICE_NAME);
  NimBLEDevice::setMTU(256);

  NimBLEServer *server = NimBLEDevice::createServer();
  server->setCallbacks(new ServerCallbacks());

  NimBLEService *service = server->createService(SERVICE_UUID);
  NimBLECharacteristic *navChar = service->createCharacteristic(
      NAV_CHAR_UUID, NIMBLE_PROPERTY::WRITE | NIMBLE_PROPERTY::WRITE_NR);
  navChar->setCallbacks(new NavCharCallbacks());

  NimBLECharacteristic *iconChar = service->createCharacteristic(
      ICON_CHAR_UUID, NIMBLE_PROPERTY::WRITE | NIMBLE_PROPERTY::WRITE_NR);
  iconChar->setCallbacks(new IconCharCallbacks());

  NimBLECharacteristic *mediaChar = service->createCharacteristic(
      MEDIA_CHAR_UUID, NIMBLE_PROPERTY::WRITE | NIMBLE_PROPERTY::WRITE_NR);
  mediaChar->setCallbacks(new MediaCharCallbacks());

  g_cmdChar = service->createCharacteristic(
      CMD_CHAR_UUID, NIMBLE_PROPERTY::NOTIFY);

  server->start();

  NimBLEAdvertising *adv = NimBLEDevice::getAdvertising();
  adv->stop();
  adv->setMinInterval(0x20); // 20 ms fast advertising for instant Android discovery
  adv->setMaxInterval(0x40); // 40 ms
  adv->enableScanResponse(true);
  NimBLEAdvertisementData advData;
  advData.setFlags(BLE_HS_ADV_F_DISC_GEN | BLE_HS_ADV_F_BREDR_UNSUP);
  advData.setCompleteServices(NimBLEUUID(SERVICE_UUID));
  NimBLEAdvertisementData scanData;
  scanData.setName(DEVICE_NAME);
  adv->setAdvertisementData(advData);
  adv->setScanResponseData(scanData);
  adv->start();

  Serial.println("[BLE] Advertising as 'Drive Assistant'");
  g_lastActivity = millis();
}

void loop() {
  if (g_pmuOk) {
    common_tick();
    // Physical PWR button short press toggles display on/off
    if (common_consume_pwr_short()) {
      common_activity();
      g_lastActivity = millis();
      setBacklight(!g_backlight);
      if (g_backlight) g_dirtyAll = true;
    }
  }

  // Poll 6-axis IMU (detects vehicle/board movement vs stationary stop)
  imuPoll();

  // Physical BOOT button (GPIO 0) short press cycles screens or wakes display
  static bool s_bootWas = false;
  bool bootNow = (digitalRead(0) == LOW);
  if (!bootNow && s_bootWas) {
    if (g_pmuOk) common_activity();
    g_lastActivity = millis();
    if (!g_backlight) {
      setBacklight(true);
      g_dirtyAll = true;
    } else {
      g_screen = (g_screen + 1) % SCR_COUNT;
      g_dirtyAll = true;
      Serial.printf("[UI] Screen %u (via BOOT button)\n", g_screen);
    }
  }
  s_bootWas = bootNow;

  // Touch gesture polling
  TouchEvent ev = touchPoll();
  if (ev != TE_NONE) {
    g_lastActivity = millis();
    if (g_pmuOk) common_activity();
  }

  switch (ev) {
    case TE_TAP:
      if (!g_backlight) {
        setBacklight(true);
        g_dirtyAll = true;
      } else if (g_screen == SCR_MEDIA) {
        screen_media_tap(g_tapX, g_tapY);
      }
      break;
    case TE_LONG_PRESS:
      if (g_backlight) setBacklight(false);
      break;
    case TE_SWIPE_LEFT:
      if (g_backlight) {
        g_screen = (g_screen + 1) % SCR_COUNT;
        g_dirtyAll = true;
        Serial.printf("[UI] Screen %u\n", g_screen);
      }
      break;
    case TE_SWIPE_RIGHT:
      if (g_backlight) {
        g_screen = (g_screen + SCR_COUNT - 1) % SCR_COUNT;
        g_dirtyAll = true;
        Serial.printf("[UI] Screen %u\n", g_screen);
      }
      break;
    default:
      break;
  }

  // Auto-sleep after 10 min without BLE connection, IMU movement, or touch
  if (g_connected || (g_hasImu && g_imuMoving)) {
    g_lastActivity = millis();
    if (g_pmuOk) common_activity();
  } else if (millis() - g_lastActivity > SLEEP_TIMEOUT_MS) {
    goToSleep();
  }

  // Clear "Navigation ended" screen after timeout
  if (g_navEnded && millis() - g_navEndedAt > NAV_END_SHOW_MS) {
    g_navEnded = false;
    g_dirtyAll = true;
  }

  // Poll battery every 2 seconds
  static uint32_t lastBat = 0;
  if (millis() - lastBat > 2000) {
    lastBat = millis();
    if (batteryPoll() && g_screen == SCR_INFO) g_dirtyAll = true;
  }

  // Redraw changed regions when display is active
  if (g_backlight &&
      (g_dirtyAll || g_dirtyIcon || g_dirtyDist || g_dirtyStreet ||
       (g_dirtyMedia && g_screen == SCR_MEDIA))) {
    redraw();
  }

  // Animate waiting screen spinner when idle on main screen
  if (g_backlight && g_screen == SCR_MAIN && !g_navShown && !g_navEnded) {
    screen_nav_animate_waiting();
  }

  delay(10);
}
