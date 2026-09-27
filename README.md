# Drive Assistant — Waveshare ESP32-S3 1.8" Touch AMOLED (V1 & V2)

A Bluetooth Low Energy (BLE) turn-by-turn navigation HUD and media controller for the **Waveshare ESP32-S3-Touch-AMOLED-1.8** (`368×448` QSPI AMOLED).

Mirrors real-time **Google Maps** turn-by-turn navigation notifications (maneuver arrows, distance countdown, street name) and **Android media playback controls** from the companion Android app over BLE.

---

## Supported Hardware

Runtime hardware detection automatically identifies your board revision at boot by probing I2C (`0x15`):

| Feature | Waveshare 1.8" AMOLED **V1** | Waveshare 1.8" AMOLED **V2** |
| :--- | :--- | :--- |
| **MCU** | ESP32-S3 (8 MB OPI PSRAM, 16 MB QIO Flash) | ESP32-S3 (8 MB OPI PSRAM, 16 MB QIO Flash) |
| **Display Panel** | **SH8601** (`368×448`, QSPI) | **CO5300** (`368×448`, QSPI, `col_offset1 = 16`) |
| **Touch Controller** | **FT3168** (`I2C @ 0x38`) + XCA9554 (`@ 0x20`) | **CST816** (`I2C @ 0x15`) |
| **Power Management** | **AXP2101 PMU** (`I2C @ 0x34`) | **AXP2101 PMU** (`I2C @ 0x34`) |
| **Motion Sensor** | **QMI8658 6-Axis IMU** (`I2C @ 0x6B`) | **QMI8658 6-Axis IMU** (`I2C @ 0x6B`) |

---

## Features

- **Crisp `200×200` Vector Maneuver Arrows**:
  - High-contrast Google Maps-style bent road stems and arrowheads rendered natively at `368×448` (`turn-left`, `turn-right`, `slight-left`, `slight-right`, `sharp-left`, `sharp-right`, `uturn`, `roundabout`, `merge`, `straight`, `arrive`).
  - Includes a `Scale3x` + bilinear iso-contour fallback renderer for custom `40×40` 1-bit bitmaps sent over BLE.
- **Stop-Aware `< 50 m` Turn Zone Countdown (`QMI8658` 6-Axis IMU)**:
  - Measures approach pace from prior `10 m` GPS steps (`80 m` $\rightarrow$ `70 m` $\rightarrow$ `60 m` $\rightarrow$ `50 m`) and smoothly counts down **`40 m` $\rightarrow$ `30 m` $\rightarrow$ `20 m` $\rightarrow$ `10 m`** in bright accent green inside the `< 50 m` turn zone.
  - Polls the onboard **QMI8658 6-axis accelerometer + gyroscope** (`@ 0x6B`) at `40 Hz` to detect if you stop before the intersection (e.g., at a red light `20 m` before the turn) and **automatically pauses the countdown** until movement resumes.
- **Music Playback Controller**:
  - Displays current track title, artist, and touch buttons for **Previous (`V`)**, **Play/Pause (`P`)**, and **Next (`N`)**.
- **Battery, IMU & System Status**:
  - Real-time battery percentage and charging indicator via the onboard **AXP2101** fuel gauge, live **QMI8658 IMU** motion state (`Moving` / `Stationary`), BLE connection state, detected hardware variant (`V1` vs `V2`), and firmware version.

---

## Controls (Touch & Physical Buttons)

| Input | Action |
| :--- | :--- |
| **Swipe Left / Right** | Switch between screens (`Navigation` $\leftrightarrow$ `Music` $\leftrightarrow$ `Status`) |
| **BOOT Button (Short Press)** | Cycle to the next screen (or wake display if off) |
| **PWR Button (Short Press)** | Toggle AMOLED display ON / OFF |
| **PWR Button (Hold > 1.5 s)** | Clean AXP2101 PMU power-off |
| **Touch Long Press (600 ms)** | Turn display off (tap anywhere to wake) |
| **Tap (on Music Screen)** | Trigger Previous / Play-Pause / Next track |

---

## Project Structure

```text
├── android/
│   └── DriveAssistant-0.21-EN-Fixed.apk   # Patched Android companion app (English Google Maps + icon fix)
├── builds/
│   └── 1.0/firmware.bin                   # Prebuilt ESP32-S3 firmware binary
├── include/
│   ├── pin_config.h                       # QSPI, I2C, and AXP2101 pin definitions
│   ├── Arduino_OLED.h                     # Base OLED brightness/power interface
│   ├── display/                           # Arduino_SH8601 (V1) & Arduino_CO5300 (V2) drivers
│   └── fonts/                             # FreeSans GFX fonts
├── src/
│   ├── main.cpp                           # Hardware setup, BLE GATT server, payload parser, main loop
│   ├── hw_panel.h / hw_panel.cpp          # Runtime V1/V2 panel & touch auto-detection
│   ├── device_common.h / .cpp             # AXP2101 PMU power management & RGB565 alpha blending
│   └── screens/                           # Modular UI Screen Components
│       ├── ui_common.h / .cpp             # Shared constants, palette, state, and text helpers
│       ├── screen_nav.h / .cpp            # Screen 0: Navigation, vector arrows & waiting spinner
│       ├── screen_media.h / .cpp          # Screen 1: Music player UI & touch controls
│       └── screen_info.h / .cpp           # Screen 2: Battery gauge, BLE & HW status
└── platformio.ini                         # PlatformIO build configuration
```

### How to Add a New Screen Component
1. Create `src/screens/screen_mynew.h` and `src/screens/screen_mynew.cpp`.
2. Add your screen ID before `SCR_COUNT` in [`src/screens/ui_common.h`](src/screens/ui_common.h):
   ```cpp
   enum ScreenId : uint8_t {
     SCR_MAIN  = 0,
     SCR_MEDIA = 1,
     SCR_INFO  = 2,
     SCR_MYNEW = 3,
     SCR_COUNT = 4
   };
   ```
3. Call `screen_mynew_draw()` inside `redraw()` in [`src/main.cpp`](src/main.cpp). (`platformio.ini` automatically compiles all `.cpp` files inside `src/screens/`.)

---

## Building & Flashing

### 1. Flash ESP32-S3 Firmware (PlatformIO)
1. Open this folder in **VS Code** with the **PlatformIO** extension installed.
2. Connect the Waveshare ESP32-S3-Touch-AMOLED-1.8 board via USB-C.
3. Run **PlatformIO: Upload** (or run `pio run -t upload`).

### 2. Install the Android Companion App
1. Install [`android/DriveAssistant-0.21-EN-Fixed.apk`](android/DriveAssistant-0.21-EN-Fixed.apk) on your Android phone (uninstall any previous version first if switching signatures).
2. Open the **Drive Assistant** app and follow the guided setup to grant **Bluetooth**, **Notification Access**, and **Background Battery** permissions.
3. Start a route in **Google Maps** (to test indoors without moving, use **Lockito** with Mock Locations enabled in Android Developer Options).

---

## BLE GATT Protocol Summary

- **Service UUID**: `6e400001-b5a3-f393-e0a9-e50e24dcca9e`
  - **Navigation Characteristic (`6e400002-...`, Write/Write_NR)**: UTF-8 string `"maneuver|distance|street"` (e.g. `"turn-right|150 m|Main St"`, `"end||"`).
  - **Icon Characteristic (`6e400003-...`, Write/Write_NR)**: `203` bytes (`'I'`, `40`, `40`, followed by `200` bytes of 1-bit row-major `40×40` bitmap data), or `3` bytes (`'I', 0, 0`) to clear.
  - **Media Characteristic (`6e400004-...`, Write/Write_NR)**: UTF-8 string `"state|title|artist"` (`state` = `play`, `pause`, or `stop`).
  - **Command Characteristic (`6e400005-...`, Notify)**: `1` byte sent from ESP32 to phone (`'P'` = Play/Pause, `'N'` = Next, `'V'` = Previous).

---

## Credits
- Original Drive Assistant concept & protocol by [DEMP1993](https://github.com/DEMP1993/Drive-assistant-device-firmware) ([Android companion app](https://github.com/DEMP1993/Drive-assistant-android-app)).
- Waveshare ESP32-S3 1.8" AMOLED V1/V2 hardware abstraction pattern adapted from [app-pixels/dice](https://github.com/app-pixels/dice).
