package com.mapsbledisplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;

/* compiled from: DisplaySettingsHelper.kt */
/* loaded from: classes.dex */
public final class DisplaySettingsHelper {
    public static TextView tvArrowLabel;
    public static TextView tvBrightness;
    public static TextView tvCpuLabel;
    public static TextView tvDistLabel;
    public static TextView tvPollLabel;
    public static int curBrightness = 255;
    public static String curArrowHex = "07E8";
    public static String curDistHex = "FFFF";
    public static int curCpuMhz = 160;
    public static int curPollHigh = 0;

    /* compiled from: DisplaySettingsHelper.kt */
    public final class ColorClickListener implements View.OnClickListener {
        private final Context ctx;
        private final String hex;
        private final boolean isArrow;

        public ColorClickListener(Context context, String str, boolean z) {
            this.ctx = context;
            this.hex = str;
            this.isArrow = z;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            boolean z = this.isArrow;
            String str = this.hex;
            if (z) {
                DisplaySettingsHelper.curArrowHex = str;
            } else {
                DisplaySettingsHelper.curDistHex = str;
            }
            DisplaySettingsHelper.updateLabels();
            DisplaySettingsHelper.sendCfg(this.ctx);
        }
    }

    /* compiled from: DisplaySettingsHelper.kt */
    public final class ModeClickListener implements View.OnClickListener {
        private final Context ctx;
        private final boolean isCpu;
        private final int modeVal;

        public ModeClickListener(Context context, int i, boolean z) {
            this.ctx = context;
            this.modeVal = i;
            this.isCpu = z;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            boolean z = this.isCpu;
            int i = this.modeVal;
            if (z) {
                DisplaySettingsHelper.curCpuMhz = i;
            } else {
                DisplaySettingsHelper.curPollHigh = i;
            }
            DisplaySettingsHelper.updateLabels();
            DisplaySettingsHelper.sendCfg(this.ctx);
        }
    }

    /* compiled from: DisplaySettingsHelper.kt */
    public final class SeekListener implements SeekBar.OnSeekBarChangeListener {
        private final Context ctx;

        public SeekListener(Context context) {
            this.ctx = context;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (i < 5) {
                i = 5;
            }
            DisplaySettingsHelper.curBrightness = i;
            DisplaySettingsHelper.updateLabels();
            if (z) {
                DisplaySettingsHelper.sendCfg(this.ctx);
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            DisplaySettingsHelper.sendCfg(this.ctx);
        }
    }

    public static void addColorBtn(Context context, LinearLayout linearLayout, String str, String str2, int i, int i2, boolean z) {
        Button button = new Button(context);
        button.setText(str);
        button.setAllCaps(false);
        button.setTextSize(11.0f);
        button.setTextColor(i2);
        button.setBackgroundColor(i);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMargins(6, 6, 6, 6);
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(new ColorClickListener(context, str2, z));
        linearLayout.addView(button);
    }

    public static void addColorBtnToRow(LinearLayout linearLayout, Context context, String str, String str2, int i, int i2, boolean z) {
        addColorBtn(context, linearLayout, str, str2, i, i2, z);
    }

    public static void addModeBtn(LinearLayout linearLayout, Context context, String str, int i, boolean z) {
        Button button = new Button(context);
        button.setText(str);
        button.setAllCaps(false);
        button.setTextSize(11.0f);
        button.setTextColor(-1);
        button.setBackgroundColor(-14277082);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMargins(6, 6, 6, 6);
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(new ModeClickListener(context, i, z));
        linearLayout.addView(button);
    }

    public static void attach(Activity activity, ScrollView scrollView) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("drive_cfg", 0);
        curBrightness = sharedPreferences.getInt("bright", 255);
        String string = sharedPreferences.getString("colArrow", "07E8");
        if (string != null) {
            curArrowHex = string;
        }
        String string2 = sharedPreferences.getString("colDist", "FFFF");
        if (string2 != null) {
            curDistHex = string2;
        }
        curCpuMhz = sharedPreferences.getInt("cpuMhz", 160);
        curPollHigh = sharedPreferences.getInt("pollHigh", 0);
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        makeLabel(activity, linearLayout, "DISPLAY & POWER SETTINGS", -16711936, 14.0f);
        tvBrightness = makeLabel(activity, linearLayout, "Brightness: 255 / 255", -1, 13.0f);
        SeekBar seekBar = new SeekBar(activity);
        seekBar.setMax(255);
        seekBar.setProgress(curBrightness);
        seekBar.setOnSeekBarChangeListener(new SeekListener(activity));
        linearLayout.addView(seekBar);
        tvCpuLabel = makeLabel(activity, linearLayout, "CPU Speed", -1, 13.0f);
        LinearLayout makeRow = makeRow(activity, linearLayout);
        addModeBtn(makeRow, activity, "LOW (80)", 80, true);
        addModeBtn(makeRow, activity, "MED (160)", 160, true);
        addModeBtn(makeRow, activity, "HIGH (240)", 240, true);
        tvPollLabel = makeLabel(activity, linearLayout, "I2C / Touch Polling", -1, 13.0f);
        LinearLayout makeRow2 = makeRow(activity, linearLayout);
        addModeBtn(makeRow2, activity, "LOW (Power Save)", 0, false);
        addModeBtn(makeRow2, activity, "HIGH (100Hz)", 1, false);
        tvArrowLabel = makeLabel(activity, linearLayout, "Maneuver Arrow Color", -1, 13.0f);
        LinearLayout makeRow3 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow3, activity, "Green", "07E8", -16711872, ViewCompat.MEASURED_STATE_MASK, true);
        addColorBtnToRow(makeRow3, activity, "White", "FFFF", -1, ViewCompat.MEASURED_STATE_MASK, true);
        addColorBtnToRow(makeRow3, activity, "Yellow", "FFE0", InputDeviceCompat.SOURCE_ANY, ViewCompat.MEASURED_STATE_MASK, true);
        LinearLayout makeRow4 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow4, activity, "Cyan", "07FF", -16711681, ViewCompat.MEASURED_STATE_MASK, true);
        addColorBtnToRow(makeRow4, activity, "Orange", "FD20", -23296, ViewCompat.MEASURED_STATE_MASK, true);
        addColorBtnToRow(makeRow4, activity, "Magenta", "F81F", -65281, ViewCompat.MEASURED_STATE_MASK, true);
        tvDistLabel = makeLabel(activity, linearLayout, "Distance Text Color", -1, 13.0f);
        LinearLayout makeRow5 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow5, activity, "White", "FFFF", -1, ViewCompat.MEASURED_STATE_MASK, false);
        addColorBtnToRow(makeRow5, activity, "Yellow", "FFE0", InputDeviceCompat.SOURCE_ANY, ViewCompat.MEASURED_STATE_MASK, false);
        addColorBtnToRow(makeRow5, activity, "Green", "07E8", -16711872, ViewCompat.MEASURED_STATE_MASK, false);
        LinearLayout makeRow6 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow6, activity, "Cyan", "07FF", -16711681, ViewCompat.MEASURED_STATE_MASK, false);
        addColorBtnToRow(makeRow6, activity, "Orange", "FD20", -23296, ViewCompat.MEASURED_STATE_MASK, false);
        addColorBtnToRow(makeRow6, activity, "Red", "F904", -57308, -1, false);
        updateLabels();
    }

    public static TextView makeLabel(Context context, LinearLayout linearLayout, String str, int i, float f) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(i);
        textView.setTextSize(f);
        textView.setPadding(8, 20, 8, 8);
        linearLayout.addView(textView);
        return textView;
    }

    public static LinearLayout makeRow(Context context, LinearLayout linearLayout) {
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        linearLayout.addView(linearLayout2);
        return linearLayout2;
    }

    public static void sendCfg(Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences("drive_cfg", 0).edit();
        edit.putInt("bright", curBrightness);
        edit.putString("colArrow", curArrowHex);
        edit.putString("colDist", curDistHex);
        edit.putInt("cpuMhz", curCpuMhz);
        edit.putInt("pollHigh", curPollHigh);
        edit.apply();
        BleManager.INSTANCE.sendRaw("cfg|b=" + curBrightness + "|a=" + curArrowHex + "|d=" + curDistHex + "|c=" + curCpuMhz + "|p=" + curPollHigh);
    }

    public static void updateLabels() {
        TextView textView = tvBrightness;
        if (textView != null) {
            textView.setText("Brightness: " + curBrightness + " / 255 (HBM Peak @ 255)");
        }
        TextView textView2 = tvCpuLabel;
        if (textView2 != null) {
            textView2.setText("CPU Speed: " + curCpuMhz + " MHz");
        }
        TextView textView3 = tvPollLabel;
        if (textView3 != null) {
            textView3.setText(curPollHigh != 0 ? "I2C / Touch Polling: HIGH (100Hz)" : "I2C / Touch Polling: LOW (Power Save)");
        }
        TextView textView4 = tvArrowLabel;
        if (textView4 != null) {
            textView4.setText("Maneuver Arrow Color (0x" + curArrowHex + ")");
        }
        TextView textView5 = tvDistLabel;
        if (textView5 != null) {
            textView5.setText("Distance Text Color (0x" + curDistHex + ")");
        }
    }
}
