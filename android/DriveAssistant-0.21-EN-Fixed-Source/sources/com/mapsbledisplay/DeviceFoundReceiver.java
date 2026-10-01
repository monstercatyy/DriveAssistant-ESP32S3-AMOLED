package com.mapsbledisplay;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DeviceFoundReceiver.kt */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\n"}, d2 = {"Lcom/mapsbledisplay/DeviceFoundReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class DeviceFoundReceiver extends BroadcastReceiver {
    public static final String ACTION_FOUND = "com.mapsbledisplay.DEVICE_FOUND";
    private static final String TAG = "DeviceFound";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        ArrayList arrayList;
        BluetoothDevice device;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        if (Intrinsics.areEqual(intent.getAction(), ACTION_FOUND)) {
            int intExtra = intent.getIntExtra("android.bluetooth.le.extra.ERROR_CODE", 0);
            if (intExtra != 0) {
                Log.w(TAG, "Hintergrund-Scan Fehler " + intExtra);
                return;
            }
            if (intent.getIntExtra("android.bluetooth.le.extra.CALLBACK_TYPE", 1) == 4) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 33) {
                ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra("android.bluetooth.le.extra.LIST_SCAN_RESULT", ScanResult.class);
                if (parcelableArrayListExtra != null) {
                    arrayList = parcelableArrayListExtra;
                } else {
                    arrayList = CollectionsKt.emptyList();
                }
            } else {
                ArrayList parcelableArrayListExtra2 = intent.getParcelableArrayListExtra("android.bluetooth.le.extra.LIST_SCAN_RESULT");
                if (parcelableArrayListExtra2 != null) {
                    arrayList = parcelableArrayListExtra2;
                } else {
                    arrayList = CollectionsKt.emptyList();
                }
            }
            ScanResult scanResult = (ScanResult) CollectionsKt.firstOrNull(arrayList);
            String address = (scanResult == null || (device = scanResult.getDevice()) == null) ? null : device.getAddress();
            if (address == null) {
                return;
            }
            Log.i(TAG, "Drive Assistant gesehen (" + address + ") -> verbinde");
            DeviceService.INSTANCE.start(context, address);
        }
    }
}
