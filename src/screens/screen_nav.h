/*
 * screen_nav.h — Navigation & Waiting Screen Component (SCR_MAIN)
 */

#pragma once

#include "ui_common.h"

// Renders the main navigation screen (waiting, active turn-by-turn, or ended)
void screen_nav_draw();

// Animates the circular progress spinner when idle on the waiting screen
void screen_nav_animate_waiting();
