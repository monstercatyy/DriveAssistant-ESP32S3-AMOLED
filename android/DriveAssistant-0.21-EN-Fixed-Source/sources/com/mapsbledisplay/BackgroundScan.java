package com.mapsbledisplay;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

/* compiled from: BackgroundScan.kt */
@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0013J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u0018\u0010\u001a\u001a\n \u001c*\u0004\u0018\u00010\u001b0\u001b2\u0006\u0010\u001d\u001a\u00020\u0013H\u0002J\u000e\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0013J\u0010\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0013H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006 "}, d2 = {"Lcom/mapsbledisplay/BackgroundScan;", "", "()V", "KEY_PAUSED", "", "PREFS", "REQUEST_CODE", "", "TAG", "_active", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "active", "Lkotlinx/coroutines/flow/StateFlow;", "getActive", "()Lkotlinx/coroutines/flow/StateFlow;", "adapter", "Landroid/bluetooth/BluetoothAdapter;", "context", "Landroid/content/Context;", "ensure", "", "isPaused", "pause", "pendingIntent", "Landroid/app/PendingIntent;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "ctx", "start", "stop", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class BackgroundScan {
    public static final BackgroundScan INSTANCE = new BackgroundScan();
    private static final String KEY_PAUSED = "paused";
    private static final String PREFS = "autoconnect";
    private static final int REQUEST_CODE = 42;
    private static final String TAG = "BackgroundScan";
    private static final MutableStateFlow<Boolean> _active;
    private static final StateFlow<Boolean> active;

    private BackgroundScan() {
    }

    static {
        MutableStateFlow<Boolean> MutableStateFlow = StateFlowKt.MutableStateFlow(false);
        _active = MutableStateFlow;
        active = FlowKt.asStateFlow(MutableStateFlow);
    }

    public final StateFlow<Boolean> getActive() {
        return active;
    }

    private final BluetoothAdapter adapter(Context context) {
        Object systemService = context.getSystemService("bluetooth");
        BluetoothManager bluetoothManager = systemService instanceof BluetoothManager ? (BluetoothManager) systemService : null;
        if (bluetoothManager != null) {
            return bluetoothManager.getAdapter();
        }
        return null;
    }

    private final PendingIntent pendingIntent(Context context) {
        Intent action = new Intent(context, (Class<?>) DeviceFoundReceiver.class).setAction(DeviceFoundReceiver.ACTION_FOUND);
        Intrinsics.checkNotNullExpressionValue(action, "setAction(...)");
        PendingIntent broadcast = PendingIntent.getBroadcast(context.getApplicationContext(), 42, action, 167772160);
        Intrinsics.checkNotNullExpressionValue(broadcast, "getBroadcast(...)");
        return broadcast;
    }

    private final SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, 0);
    }

    public final boolean isPaused(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Context applicationContext = context.getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        return prefs(applicationContext).getBoolean(KEY_PAUSED, false);
    }

    public final void ensure(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (isPaused(context)) {
            Log.i(TAG, "Auto-Verbinden pausiert - Scan nicht registriert");
        } else {
            start(context);
        }
    }

    public final void pause(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Context applicationContext = context.getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        prefs(applicationContext).edit().putBoolean(KEY_PAUSED, true).apply();
        stop(context);
    }

    public final void start(Context context) {
        int i;
        Intrinsics.checkNotNullParameter(context, "context");
        Context applicationContext = context.getApplicationContext();
        Intrinsics.checkNotNull(applicationContext);
        prefs(applicationContext).edit().putBoolean(KEY_PAUSED, false).apply();
        if (!Permissions.INSTANCE.hasBle(applicationContext)) {
            Log.w(TAG, "Bluetooth-Berechtigung fehlt - kein Hintergrund-Scan");
            return;
        }
        BluetoothAdapter adapter = adapter(applicationContext);
        BluetoothLeScanner bluetoothLeScanner = adapter != null ? adapter.getBluetoothLeScanner() : null;
        if (adapter == null || !adapter.isEnabled() || bluetoothLeScanner == null) {
            Log.w(TAG, "Bluetooth aus - kein Hintergrund-Scan");
            _active.setValue(false);
            return;
        }
        PendingIntent pendingIntent = pendingIntent(applicationContext);
        try {
            bluetoothLeScanner.stopScan(pendingIntent);
        } catch (Exception unused) {
        }
        ScanFilter build = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BleManager.INSTANCE.getSERVICE_UUID())).build();
        boolean isOffloadedFilteringSupported = adapter.isOffloadedFilteringSupported();
        ScanSettings.Builder scanMode = new ScanSettings.Builder().setScanMode(0);
        if (isOffloadedFilteringSupported) {
            scanMode.setCallbackType(6).setMatchMode(1).setNumOfMatches(1);
        }
        try {
            i = bluetoothLeScanner.startScan(CollectionsKt.listOf(build), scanMode.build(), pendingIntent);
        } catch (Exception e) {
            Log.w(TAG, "startScan(PendingIntent) Fehler: " + e.getMessage());
            i = -1;
        }
        _active.setValue(Boolean.valueOf(i == 0));
        Log.i(TAG, "Hintergrund-Scan registriert: rc=" + i + " offloaded=" + isOffloadedFilteringSupported);
    }

    private final void stop(Context context) {
        BluetoothLeScanner bluetoothLeScanner;
        Context applicationContext = context.getApplicationContext();
        try {
            Intrinsics.checkNotNull(applicationContext);
            BluetoothAdapter adapter = adapter(applicationContext);
            if (adapter != null && (bluetoothLeScanner = adapter.getBluetoothLeScanner()) != null) {
                bluetoothLeScanner.stopScan(pendingIntent(applicationContext));
            }
        } catch (Exception e) {
            Log.w(TAG, "stopScan: " + e.getMessage());
        }
        _active.setValue(false);
        Log.i(TAG, "Hintergrund-Scan gestoppt");
    }
}
