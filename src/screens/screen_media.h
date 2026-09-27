/*
 * screen_media.h — Music Playback Control Screen Component (SCR_MEDIA)
 */

#pragma once

#include "ui_common.h"

// Renders the music playback screen (track title, artist, transport controls)
void screen_media_draw();

// Handles touch tap events on the music playback screen
void screen_media_tap(int16_t x, int16_t y);
