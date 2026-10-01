/*
 * screen_media.cpp — Music Playback Control Screen Component (SCR_MEDIA)
 */

#include "screen_media.h"

#define MEDIA_BTN_CY   290
#define MEDIA_BTN_TOP  230
#define MEDIA_BTN_BOT  350

void screen_media_draw() {
  gfx->fillScreen(COL_BG);
  drawTextC("NOW PLAYING", 66, &SFCompactBold12pt7b, COL_DIST);

  if (g_mediaState == 0) {
    drawTextC("Nothing playing", CY + 10, &SFCompactBold12pt7b, COL_STREET);
    return;
  }

  drawTextC(fitStreet(g_mediaTitle.length() ? g_mediaTitle : "Unknown",
                      &SFCompactBold12pt7b, 320), 145, &SFCompactBold12pt7b, COL_DIST);
  drawTextC(fitStreet(g_mediaArtist, &SFCompactBold9pt7b, 300),
            190, &SFCompactBold9pt7b, COL_STREET);

  const int16_t y     = MEDIA_BTN_CY;
  const int16_t prevX = CX - 104;  // 80
  const int16_t nextX = CX + 104;  // 288

  // Previous button (two triangles pointing left)
  gfx->fillTriangle(prevX + 22, y - 20, prevX + 22, y + 20, prevX - 4,  y, COL_STREET);
  gfx->fillTriangle(prevX - 2,  y - 20, prevX - 2,  y + 20, prevX - 28, y, COL_STREET);

  // Play / Pause button in a large central circle
  gfx->drawCircle(CX, y, 44, COL_ARROW);
  gfx->drawCircle(CX, y, 43, COL_ARROW);
  gfx->drawCircle(CX, y, 42, COL_ARROW);
  if (g_mediaState == 2) {  // Playing -> show pause bars
    gfx->fillRect(CX - 16, y - 18, 11, 36, COL_ARROW);
    gfx->fillRect(CX + 5,  y - 18, 11, 36, COL_ARROW);
  } else {                  // Paused -> show play triangle
    gfx->fillTriangle(CX - 12, y - 20, CX - 12, y + 20, CX + 20, y, COL_ARROW);
  }

  // Next button (two triangles pointing right)
  gfx->fillTriangle(nextX - 22, y - 20, nextX - 22, y + 20, nextX + 4,  y, COL_STREET);
  gfx->fillTriangle(nextX + 2,  y - 20, nextX + 2,  y + 20, nextX + 28, y, COL_STREET);
}

void screen_media_tap(int16_t x, int16_t y) {
  if (g_mediaState == 0) return;
  if (y < MEDIA_BTN_TOP || y > MEDIA_BTN_BOT) return;

  char cmd;
  if (x < CX - 52)      cmd = 'V';   // Previous track
  else if (x < CX + 52) cmd = 'P';   // Play / Pause
  else                  cmd = 'N';   // Next track

  sendMediaCommand(cmd);
}
