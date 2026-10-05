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
    public static TextView tvBarLabel;
    public static TextView tvBarDirLabel;
    public static int curBrightness = 75;
    public static String curArrowHex = "07E8";
    public static String curDistHex = "FFFF";
    public static String curBarHex = "077F";
    public static int curBarDir = 0;
    public static int curCpuMhz = 80;
    public static int curPollHigh = 0;

    /* compiled from: DisplaySettingsHelper.kt */
    public final class ColorClickListener implements View.OnClickListener {
        private final Context ctx;
        private final String hex;
        private final int target;

        public ColorClickListener(Context context, String str, int target) {
            this.ctx = context;
            this.hex = str;
            this.target = target;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            String str = this.hex;
            if (this.target == 0) {
                DisplaySettingsHelper.curArrowHex = str;
            } else if (this.target == 1) {
                DisplaySettingsHelper.curDistHex = str;
            } else if (this.target == 2) {
                DisplaySettingsHelper.curBarHex = str;
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

    public static void addColorBtn(Context context, LinearLayout linearLayout, String str, String str2, int i, int i2, int target) {
        Button button = new Button(context);
        button.setText(str);
        button.setAllCaps(false);
        button.setTextSize(11.0f);
        button.setTextColor(i2);
        button.setBackgroundColor(i);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMargins(6, 6, 6, 6);
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(new ColorClickListener(context, str2, target));
        linearLayout.addView(button);
    }

    public static void addColorBtnToRow(LinearLayout linearLayout, Context context, String str, String str2, int i, int i2, int target) {
        addColorBtn(context, linearLayout, str, str2, i, i2, target);
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

    /* compiled from: DisplaySettingsHelper.kt */
    public final class DirClickListener implements View.OnClickListener {
        private final Context ctx;
        private final int dirVal;

        public DirClickListener(Context context, int i) {
            this.ctx = context;
            this.dirVal = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            DisplaySettingsHelper.curBarDir = this.dirVal;
            DisplaySettingsHelper.updateLabels();
            DisplaySettingsHelper.sendCfg(this.ctx);
        }
    }

    public static void addDirBtn(LinearLayout linearLayout, Context context, String str, int i) {
        Button button = new Button(context);
        button.setText(str);
        button.setAllCaps(false);
        button.setTextSize(11.0f);
        button.setTextColor(-1);
        button.setBackgroundColor(-14277082);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMargins(6, 6, 6, 6);
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(new DirClickListener(context, i));
        linearLayout.addView(button);
    }

    public static void attach(Activity activity, ScrollView scrollView) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("drive_cfg", 0);
        curBrightness = sharedPreferences.getInt("bright", 75);
        String string = sharedPreferences.getString("colArrow", "07E8");
        if (string != null) {
            curArrowHex = string;
        }
        String string2 = sharedPreferences.getString("colDist", "FFFF");
        if (string2 != null) {
            curDistHex = string2;
        }
        String string3 = sharedPreferences.getString("colBar", "077F");
        if (string3 != null) {
            curBarHex = string3;
        }
        curCpuMhz = sharedPreferences.getInt("cpuMhz", 80);
        curPollHigh = sharedPreferences.getInt("pollHigh", 0);
        curBarDir = sharedPreferences.getInt("barDir", 0);
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
        addColorBtnToRow(makeRow3, activity, "Green", "07E8", -16711872, ViewCompat.MEASURED_STATE_MASK, 0);
        addColorBtnToRow(makeRow3, activity, "White", "FFFF", -1, ViewCompat.MEASURED_STATE_MASK, 0);
        addColorBtnToRow(makeRow3, activity, "Yellow", "FFE0", InputDeviceCompat.SOURCE_ANY, ViewCompat.MEASURED_STATE_MASK, 0);
        LinearLayout makeRow4 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow4, activity, "Cyan", "07FF", -16711681, ViewCompat.MEASURED_STATE_MASK, 0);
        addColorBtnToRow(makeRow4, activity, "Orange", "FD20", -23296, ViewCompat.MEASURED_STATE_MASK, 0);
        addColorBtnToRow(makeRow4, activity, "Magenta", "F81F", -65281, ViewCompat.MEASURED_STATE_MASK, 0);
        tvDistLabel = makeLabel(activity, linearLayout, "Distance Text Color", -1, 13.0f);
        LinearLayout makeRow5 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow5, activity, "White", "FFFF", -1, ViewCompat.MEASURED_STATE_MASK, 1);
        addColorBtnToRow(makeRow5, activity, "Yellow", "FFE0", InputDeviceCompat.SOURCE_ANY, ViewCompat.MEASURED_STATE_MASK, 1);
        addColorBtnToRow(makeRow5, activity, "Green", "07E8", -16711872, ViewCompat.MEASURED_STATE_MASK, 1);
        LinearLayout makeRow6 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow6, activity, "Cyan", "07FF", -16711681, ViewCompat.MEASURED_STATE_MASK, 1);
        addColorBtnToRow(makeRow6, activity, "Orange", "FD20", -23296, ViewCompat.MEASURED_STATE_MASK, 1);
        addColorBtnToRow(makeRow6, activity, "Red", "F904", -57308, -1, 1);
        tvBarLabel = makeLabel(activity, linearLayout, "Distance Arc Bar Color", -1, 13.0f);
        LinearLayout makeRow7 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow7, activity, "Cyan", "077F", -16711681, ViewCompat.MEASURED_STATE_MASK, 2);
        addColorBtnToRow(makeRow7, activity, "Green", "07E8", -16711872, ViewCompat.MEASURED_STATE_MASK, 2);
        addColorBtnToRow(makeRow7, activity, "Yellow", "FFE0", InputDeviceCompat.SOURCE_ANY, ViewCompat.MEASURED_STATE_MASK, 2);
        LinearLayout makeRow8 = makeRow(activity, linearLayout);
        addColorBtnToRow(makeRow8, activity, "White", "FFFF", -1, ViewCompat.MEASURED_STATE_MASK, 2);
        addColorBtnToRow(makeRow8, activity, "Orange", "FD20", -23296, ViewCompat.MEASURED_STATE_MASK, 2);
        addColorBtnToRow(makeRow8, activity, "Red", "F904", -57308, -1, 2);
        tvBarDirLabel = makeLabel(activity, linearLayout, "Distance Arc Bar Direction", -1, 13.0f);
        LinearLayout makeRowDir = makeRow(activity, linearLayout);
        addDirBtn(makeRowDir, activity, "Left > Right (Default)", 0);
        addDirBtn(makeRowDir, activity, "Right > Left", 1);
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
        edit.putString("colBar", curBarHex);
        edit.putInt("barDir", curBarDir);
        edit.putInt("cpuMhz", curCpuMhz);
        edit.putInt("pollHigh", curPollHigh);
        edit.apply();
        BleManager.INSTANCE.sendRaw("cfg|b=" + curBrightness + "|a=" + curArrowHex + "|d=" + curDistHex + "|g=" + curBarHex + "|c=" + curCpuMhz + "|p=" + curPollHigh + "|r=" + curBarDir);
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
        TextView textView6 = tvBarLabel;
        if (textView6 != null) {
            textView6.setText("Distance Arc Bar Color (0x" + curBarHex + ")");
        }
        TextView textView7 = tvBarDirLabel;
        if (textView7 != null) {
            textView7.setText(curBarDir != 0 ? "Distance Arc Bar: Right > Left" : "Distance Arc Bar: Left > Right (Default)");
        }
    }
}
