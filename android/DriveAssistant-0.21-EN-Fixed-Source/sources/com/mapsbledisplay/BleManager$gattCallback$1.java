package com.mapsbledisplay;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import com.mapsbledisplay.BleManager;
import com.mapsbledisplay.DeviceService;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.MutableStateFlow;

/* compiled from: BleManager.kt */
@Metadata(d1 = {"\u00005\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0017J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J \u0010\n\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fH\u0016J \u0010\r\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\fH\u0016J \u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\fH\u0016J \u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0016¨\u0006\u0015"}, d2 = {"com/mapsbledisplay/BleManager$gattCallback$1", "Landroid/bluetooth/BluetoothGattCallback;", "onCharacteristicChanged", "", "g", "Landroid/bluetooth/BluetoothGatt;", "c", "Landroid/bluetooth/BluetoothGattCharacteristic;", "value", "", "onCharacteristicWrite", NotificationCompat.CATEGORY_STATUS, "", "onConnectionStateChange", "newState", "onDescriptorWrite", "d", "Landroid/bluetooth/BluetoothGattDescriptor;", "onMtuChanged", "mtu", "onServicesDiscovered", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class BleManager$gattCallback$1 extends BluetoothGattCallback {
    BleManager$gattCallback$1() {
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onConnectionStateChange(BluetoothGatt g, int status, int newState) {
        BluetoothGatt bluetoothGatt;
        MutableStateFlow mutableStateFlow;
        Intrinsics.checkNotNullParameter(g, "g");
        if (newState != 0) {
            if (newState != 2) {
                return;
            }
            Log.i("BleManager", "Verbunden, frage MTU an");
            g.requestMtu(256);
            return;
        }
        Log.w("BleManager", "Getrennt (status=" + status + ")");
        BleManager bleManager = BleManager.INSTANCE;
        BleManager.navChar = null;
        BleManager bleManager2 = BleManager.INSTANCE;
        BleManager.iconChar = null;
        BleManager bleManager3 = BleManager.INSTANCE;
        BleManager.mediaChar = null;
        BleManager bleManager4 = BleManager.INSTANCE;
        BleManager.cmdChar = null;
        BleManager bleManager5 = BleManager.INSTANCE;
        BleManager.writeInFlight = false;
        g.close();
        bluetoothGatt = BleManager.gatt;
        if (bluetoothGatt == g) {
            BleManager bleManager6 = BleManager.INSTANCE;
            BleManager.gatt = null;
        }
        mutableStateFlow = BleManager._state;
        mutableStateFlow.setValue(BleManager.State.DISCONNECTED);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onMtuChanged(BluetoothGatt g, int mtu, int status) {
        Intrinsics.checkNotNullParameter(g, "g");
        Log.i("BleManager", "MTU = " + mtu + ", starte Service-Discovery");
        g.discoverServices();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onServicesDiscovered(BluetoothGatt g, int status) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        BluetoothGattCharacteristic bluetoothGattCharacteristic2;
        MutableStateFlow mutableStateFlow;
        Context context;
        String str;
        byte[] bArr;
        String str2;
        boolean subscribeCmd;
        String str3;
        byte[] bArr2;
        String str4;
        Intrinsics.checkNotNullParameter(g, "g");
        BluetoothGattService service = g.getService(BleManager.INSTANCE.getSERVICE_UUID());
        BluetoothGattCharacteristic characteristic = service != null ? service.getCharacteristic(BleManager.INSTANCE.getNAV_CHAR_UUID()) : null;
        if (characteristic == null) {
            Log.e("BleManager", "Navi-Charakteristik nicht gefunden");
            BleManager.INSTANCE.disconnect();
            return;
        }
        BleManager bleManager = BleManager.INSTANCE;
        BleManager.navChar = characteristic;
        BleManager bleManager2 = BleManager.INSTANCE;
        BleManager.iconChar = service.getCharacteristic(BleManager.INSTANCE.getICON_CHAR_UUID());
        bluetoothGattCharacteristic = BleManager.iconChar;
        if (bluetoothGattCharacteristic == null) {
            Log.w("BleManager", "Icon-Charakteristik nicht vorhanden (alte Firmware?)");
        }
        BleManager bleManager3 = BleManager.INSTANCE;
        BleManager.mediaChar = service.getCharacteristic(BleManager.INSTANCE.getMEDIA_CHAR_UUID());
        BleManager bleManager4 = BleManager.INSTANCE;
        BleManager.cmdChar = service.getCharacteristic(BleManager.INSTANCE.getCMD_CHAR_UUID());
        bluetoothGattCharacteristic2 = BleManager.mediaChar;
        if (bluetoothGattCharacteristic2 == null) {
            Log.w("BleManager", "Media-Charakteristik nicht vorhanden (alte Firmware?)");
        }
        mutableStateFlow = BleManager._state;
        mutableStateFlow.setValue(BleManager.State.CONNECTED);
        Log.i("BleManager", "Bereit zum Senden");
        DeviceService.Companion companion = DeviceService.INSTANCE;
        context = BleManager.appContext;
        if (context == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appContext");
            context = null;
        }
        DeviceService.Companion.start$default(companion, context, null, 2, null);
        str = BleManager.pendingPayload;
        if (str == null) {
            BleManager bleManager5 = BleManager.INSTANCE;
            str4 = BleManager.lastPayload;
            BleManager.pendingPayload = str4;
        }
        bArr = BleManager.pendingIcon;
        if (bArr == null) {
            BleManager bleManager6 = BleManager.INSTANCE;
            bArr2 = BleManager.lastIcon;
            BleManager.pendingIcon = bArr2;
        }
        str2 = BleManager.pendingMedia;
        if (str2 == null) {
            BleManager bleManager7 = BleManager.INSTANCE;
            str3 = BleManager.lastMedia;
            BleManager.pendingMedia = str3;
        }
        subscribeCmd = BleManager.INSTANCE.subscribeCmd(g);
        if (subscribeCmd) {
            return;
        }
        BleManager.INSTANCE.flush();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicWrite(BluetoothGatt g, BluetoothGattCharacteristic c, int status) {
        String str;
        byte[] bArr;
        String str2;
        Intrinsics.checkNotNullParameter(g, "g");
        Intrinsics.checkNotNullParameter(c, "c");
        BleManager bleManager = BleManager.INSTANCE;
        BleManager.writeInFlight = false;
        str = BleManager.pendingPayload;
        if (str == null) {
            bArr = BleManager.pendingIcon;
            if (bArr == null) {
                str2 = BleManager.pendingMedia;
                if (str2 == null) {
                    return;
                }
            }
        }
        BleManager.INSTANCE.flush();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorWrite(BluetoothGatt g, BluetoothGattDescriptor d, int status) {
        Intrinsics.checkNotNullParameter(g, "g");
        Intrinsics.checkNotNullParameter(d, "d");
        BleManager bleManager = BleManager.INSTANCE;
        BleManager.writeInFlight = false;
        Log.i("BleManager", "Kommando-Notifications abonniert (status=" + status + ")");
        BleManager.INSTANCE.flush();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c, final byte[] value) {
        Handler handler;
        Intrinsics.checkNotNullParameter(g, "g");
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(value, "value");
        if (Intrinsics.areEqual(c.getUuid(), BleManager.INSTANCE.getCMD_CHAR_UUID())) {
            if (!(value.length == 0)) {
                handler = BleManager.main;
                handler.post(new Runnable() { // from class: com.mapsbledisplay.BleManager$gattCallback$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleManager$gattCallback$1.onCharacteristicChanged$lambda$0(value);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCharacteristicChanged$lambda$0(byte[] value) {
        Intrinsics.checkNotNullParameter(value, "$value");
        MediaWatcher.INSTANCE.handleCommand(value[0]);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    @Deprecated(message = "Deprecated in Java")
    public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c) {
        Handler handler;
        Intrinsics.checkNotNullParameter(g, "g");
        Intrinsics.checkNotNullParameter(c, "c");
        final byte[] value = c.getValue();
        if (value != null && Build.VERSION.SDK_INT < 33 && Intrinsics.areEqual(c.getUuid(), BleManager.INSTANCE.getCMD_CHAR_UUID())) {
            if (!(value.length == 0)) {
                handler = BleManager.main;
                handler.post(new Runnable() { // from class: com.mapsbledisplay.BleManager$gattCallback$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleManager$gattCallback$1.onCharacteristicChanged$lambda$1(value);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCharacteristicChanged$lambda$1(byte[] v) {
        Intrinsics.checkNotNullParameter(v, "$v");
        MediaWatcher.INSTANCE.handleCommand(v[0]);
    }
}
