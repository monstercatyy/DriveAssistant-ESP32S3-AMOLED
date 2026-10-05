package com.mapsbledisplay;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class IconClassifier {
    private static final String TAG = "IconClassifier";

    public static String detectManeuver(Context context, StatusBarNotification sbn) {
        if (sbn == null) return "unknown";
        Notification notification = sbn.getNotification();
        if (notification == null) return "unknown";

        // 1. Check all extras text in bundle
        Bundle extras = notification.extras;
        if (extras != null) {
            for (String key : extras.keySet()) {
                try {
                    Object val = extras.get(key);
                    if (val instanceof CharSequence) {
                        String s = val.toString();
                        String m = detectTextManeuver(s);
                        if (!"unknown".equals(m)) {
                            Log.i(TAG, "Maneuver from extra [" + key + "]: " + m);
                            return m;
                        }
                    }
                } catch (Throwable ignored) {}
            }
        }

        // 2. Check largeIcon
        Icon largeIcon = notification.getLargeIcon();
        if (largeIcon != null) {
            // Check resource name if TYPE_RESOURCE (2)
            try {
                if (largeIcon.getType() == 2) {
                    int resId = largeIcon.getResId();
                    Context pkgCtx = context.createPackageContext(sbn.getPackageName(), 0);
                    String resName = pkgCtx.getResources().getResourceEntryName(resId);
                    Log.i(TAG, "largeIcon resource name: " + resName);
                    String m = detectTextManeuver(resName.replace('_', ' '));
                    if (!"unknown".equals(m)) {
                        return m;
                    }
                }
            } catch (Throwable ignored) {}

            // Load drawable and classify 40x40 bitmap
            try {
                Drawable d = largeIcon.loadDrawable(context);
                if (d != null) {
                    Bitmap bm = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
                    d.setBounds(0, 0, 40, 40);
                    d.draw(new Canvas(bm));
                    String m = classifyArrowBitmap(bm);
                    bm.recycle();
                    if (!"unknown".equals(m)) {
                        Log.i(TAG, "Maneuver from bitmap classifier: " + m);
                        return m;
                    }
                }
            } catch (Throwable t) {
                Log.w(TAG, "Bitmap classification error: " + t);
            }
        }

        return "unknown";
    }

    public static String detectTextManeuver(String text) {
        if (text == null) return "unknown";
        String s = text.toLowerCase();
        if (s.contains("u-turn") || s.contains("uturn") || s.contains("wenden") || s.contains("kehrt") || s.contains("↺") || s.contains("↻"))
            return "uturn";
        if (s.contains("roundabout") || s.contains("kreisverkehr") || s.contains("rotary"))
            return "roundabout";
        if (s.contains("arrive") || s.contains("ziel") || s.contains("angekommen") || s.contains("destination"))
            return "arrive";
        if (s.contains("sharp right") || s.contains("scharf rechts") || s.contains("sharp_right") || s.contains("hard right") || s.contains("⮡") || s.contains("↘"))
            return "sharp-right";
        if (s.contains("sharp left") || s.contains("scharf links") || s.contains("sharp_left") || s.contains("hard left") || s.contains("⮠") || s.contains("↙"))
            return "sharp-left";
        if (s.contains("slight right") || s.contains("leicht rechts") || s.contains("slight_right") || s.contains("halbrechts") || s.contains("halb rechts") || s.contains("bear right") || s.contains("keep right") || s.contains("stay right") || s.contains("fork right") || s.contains("↗"))
            return "slight-right";
        if (s.contains("slight left") || s.contains("leicht links") || s.contains("slight_left") || s.contains("halblinks") || s.contains("halb links") || s.contains("bear left") || s.contains("keep left") || s.contains("stay left") || s.contains("fork left") || s.contains("↖"))
            return "slight-left";
        if (s.contains("turn right") || s.contains("rechts") || s.contains("right") || s.contains("↱") || s.contains("↷"))
            return "turn-right";
        if (s.contains("turn left") || s.contains("links") || s.contains("left") || s.contains("↰") || s.contains("↶"))
            return "turn-left";
        if (s.contains("straight") || s.contains("geradeaus") || s.contains("continue") || s.contains("weiter") || s.contains("head") || s.contains("follow") || s.contains("↑") || s.contains("⬆"))
            return "straight";
        return "unknown";
    }

    public static String classifyArrowBitmap(Bitmap bitmap) {
        if (bitmap == null) return "unknown";
        int totalFg = 0;
        int topFg = 0, botFg = 0;
        int topLeftFg = 0, topRightFg = 0;
        int botLeftFg = 0, botRightFg = 0;
        long topSumX = 0, botSumX = 0;

        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 40; x++) {
                int p = bitmap.getPixel(x, y);
                int a = (p >> 24) & 0xFF;
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                int lum = (r + g + b) / 3;

                boolean isFg = (a > 80 && lum > 100);
                if (isFg) {
                    totalFg++;
                    if (y < 20) {
                        topFg++;
                        topSumX += x;
                        if (x < 20) topLeftFg++;
                        else topRightFg++;
                    } else {
                        botFg++;
                        botSumX += x;
                        if (x < 20) botLeftFg++;
                        else botRightFg++;
                    }
                }
            }
        }

        if (totalFg < 25 || totalFg > 1400) {
            return "unknown";
        }

        float topCx = (topFg > 0) ? ((float) topSumX / topFg) : 20.0f;
        float botCx = (botFg > 0) ? ((float) botSumX / botFg) : 20.0f;
        float deltaX = topCx - botCx; // < 0: left, > 0: right

        Log.d(TAG, "classify: total=" + totalFg + " topFg=" + topFg + " botFg=" + botFg +
                   " TL=" + topLeftFg + " TR=" + topRightFg + " BL=" + botLeftFg + " BR=" + botRightFg +
                   " deltaX=" + deltaX);

        // U-turn: arrow curves around and points down
        if (botFg > topFg && botLeftFg > 20 && botRightFg > 20) {
            return "uturn";
        }

        // Left turn: top leans left, or top-left has arrow head
        if (deltaX < -3.5f || (topLeftFg > topRightFg * 2 && topLeftFg >= 20)) {
            if (deltaX < -10.0f || (topLeftFg > topRightFg * 4 && botRightFg > 25)) {
                return "turn-left";
            }
            if (botLeftFg > 30 && deltaX < -7.0f) {
                return "sharp-left";
            }
            return (deltaX < -6.0f) ? "turn-left" : "slight-left";
        }

        // Right turn: top leans right, or top-right has arrow head
        if (deltaX > 3.5f || (topRightFg > topLeftFg * 2 && topRightFg >= 20)) {
            if (deltaX > 10.0f || (topRightFg > topLeftFg * 4 && botLeftFg > 25)) {
                return "turn-right";
            }
            if (botRightFg > 30 && deltaX > 7.0f) {
                return "sharp-right";
            }
            return (deltaX > 6.0f) ? "turn-right" : "slight-right";
        }

        // Centered / straight:
        if (Math.abs(deltaX) <= 3.5f) {
            if (topFg > 25 && Math.abs(topCx - 20.0f) < 4.0f) {
                return "straight";
            }
            if (topLeftFg > topRightFg * 1.5f) return "slight-left";
            if (topRightFg > topLeftFg * 1.5f) return "slight-right";
            return "straight";
        }

        return "unknown";
    }
}
