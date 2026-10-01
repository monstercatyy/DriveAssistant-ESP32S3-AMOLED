package com.mapsbledisplay;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mapsbledisplay.BleManager;
import java.util.UUID;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

/* compiled from: BleManager.kt */
@Metadata(d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u000e*\u0002&9\bÇ\u0002\u0018\u00002\u00020\u0001:\u0001ZB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CH\u0002J\u000e\u0010D\u001a\u00020A2\u0006\u0010E\u001a\u00020\tJ\u0006\u0010F\u001a\u00020AJ\b\u0010G\u001a\u00020AH\u0002J\u000e\u0010H\u001a\u00020A2\u0006\u0010I\u001a\u00020 J\u0006\u0010J\u001a\u00020<J\u000e\u0010K\u001a\u00020A2\u0006\u0010L\u001a\u00020MJ\u000e\u0010N\u001a\u00020A2\u0006\u0010O\u001a\u00020*J\u000e\u0010P\u001a\u00020A2\u0006\u0010Q\u001a\u00020\tJ\u000e\u0010R\u001a\u00020A2\u0006\u0010Q\u001a\u00020\tJ\u0006\u0010S\u001a\u00020AJ\b\u0010T\u001a\u00020AH\u0002J\u0010\u0010U\u001a\u00020<2\u0006\u0010V\u001a\u00020$H\u0002J \u0010W\u001a\u00020<2\u0006\u0010V\u001a\u00020$2\u0006\u0010X\u001a\u00020\"2\u0006\u0010Y\u001a\u00020*H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0007R\u0011\u0010\f\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0007R\u0011\u0010\u000e\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0007R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082T¢\u0006\u0002\n\u0000R\u0011\u0010\u0012\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0007R\u000e\u0010\u0014\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u0019\u001a\u0004\u0018\u00010\u001a8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001d\u0010\u001e\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001f\u001a\u00020 X\u0082.¢\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u00020&X\u0082\u0004¢\u0006\u0004\n\u0002\u0010'R\u0010\u0010(\u001a\u0004\u0018\u00010\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010*X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010+\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010,\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0019\u0010-\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0.¢\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u000e\u00101\u001a\u000202X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u00103\u001a\u0004\u0018\u00010\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00104\u001a\u0004\u0018\u00010\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00105\u001a\u0004\u0018\u00010*X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00106\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00107\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00108\u001a\u000209X\u0082\u0004¢\u0006\u0004\n\u0002\u0010:R\u000e\u0010;\u001a\u00020<X\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010=\u001a\b\u0012\u0004\u0012\u00020\u00180.¢\u0006\b\n\u0000\u001a\u0004\b>\u00100R\u000e\u0010?\u001a\u00020<X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006["}, d2 = {"Lcom/mapsbledisplay/BleManager;", "", "()V", "CCCD_UUID", "Ljava/util/UUID;", "CMD_CHAR_UUID", "getCMD_CHAR_UUID", "()Ljava/util/UUID;", "DEVICE_NAME", "", "ICON_CHAR_UUID", "getICON_CHAR_UUID", "MEDIA_CHAR_UUID", "getMEDIA_CHAR_UUID", "NAV_CHAR_UUID", "getNAV_CHAR_UUID", "SCAN_TIMEOUT_MS", "", "SERVICE_UUID", "getSERVICE_UUID", "TAG", "_lastSent", "Lkotlinx/coroutines/flow/MutableStateFlow;", "_state", "Lcom/mapsbledisplay/BleManager$State;", "adapter", "Landroid/bluetooth/BluetoothAdapter;", "getAdapter", "()Landroid/bluetooth/BluetoothAdapter;", "adapter$delegate", "Lkotlin/Lazy;", "appContext", "Landroid/content/Context;", "cmdChar", "Landroid/bluetooth/BluetoothGattCharacteristic;", "gatt", "Landroid/bluetooth/BluetoothGatt;", "gattCallback", "com/mapsbledisplay/BleManager$gattCallback$1", "Lcom/mapsbledisplay/BleManager$gattCallback$1;", "iconChar", "lastIcon", "", "lastMedia", "lastPayload", "lastSent", "Lkotlinx/coroutines/flow/StateFlow;", "getLastSent", "()Lkotlinx/coroutines/flow/StateFlow;", "main", "Landroid/os/Handler;", "mediaChar", "navChar", "pendingIcon", "pendingMedia", "pendingPayload", "scanCallback", "com/mapsbledisplay/BleManager$scanCallback$1", "Lcom/mapsbledisplay/BleManager$scanCallback$1;", "scanning", "", "state", "getState", "writeInFlight", "connect", "", "device", "Landroid/bluetooth/BluetoothDevice;", "connectTo", DeviceService.EXTRA_ADDRESS, "disconnect", "flush", "init", "context", "isBluetoothOn", "send", "nav", "Lcom/mapsbledisplay/NavData;", "sendIcon", "data", "sendMedia", "payload", "sendRaw", "startScanAndConnect", "stopScan", "subscribeCmd", "g", "writeChar", "ch", "bytes", "State", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class BleManager {
    private static final UUID CCCD_UUID;
    private static final UUID CMD_CHAR_UUID;
    private static final String DEVICE_NAME = "Drive Assistant";
    private static final UUID ICON_CHAR_UUID;
    public static final BleManager INSTANCE = new BleManager();
    private static final UUID MEDIA_CHAR_UUID;
    private static final UUID NAV_CHAR_UUID;
    private static final long SCAN_TIMEOUT_MS = 15000;
    private static final UUID SERVICE_UUID;
    private static final String TAG = "BleManager";
    private static final MutableStateFlow<String> _lastSent;
    private static final MutableStateFlow<State> _state;

    /* renamed from: adapter$delegate, reason: from kotlin metadata */
    private static final Lazy adapter;
    private static Context appContext;
    private static BluetoothGattCharacteristic cmdChar;
    private static BluetoothGatt gatt;
    private static final BleManager$gattCallback$1 gattCallback;
    private static BluetoothGattCharacteristic iconChar;
    private static byte[] lastIcon;
    private static String lastMedia;
    private static String lastPayload;
    private static final StateFlow<String> lastSent;
    private static final Handler main;
    private static BluetoothGattCharacteristic mediaChar;
    private static BluetoothGattCharacteristic navChar;
    private static byte[] pendingIcon;
    private static String pendingMedia;
    private static String pendingPayload;
    private static final BleManager$scanCallback$1 scanCallback;
    private static boolean scanning;
    private static final StateFlow<State> state;
    private static int testIdx;
    private static boolean writeInFlight;

    private BleManager() {
    }

    /* JADX WARN: Type inference failed for: r0v23, types: [com.mapsbledisplay.BleManager$scanCallback$1] */
    static {
        UUID fromString = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
        Intrinsics.checkNotNullExpressionValue(fromString, "fromString(...)");
        SERVICE_UUID = fromString;
        UUID fromString2 = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
        Intrinsics.checkNotNullExpressionValue(fromString2, "fromString(...)");
        NAV_CHAR_UUID = fromString2;
        UUID fromString3 = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
        Intrinsics.checkNotNullExpressionValue(fromString3, "fromString(...)");
        ICON_CHAR_UUID = fromString3;
        UUID fromString4 = UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e");
        Intrinsics.checkNotNullExpressionValue(fromString4, "fromString(...)");
        MEDIA_CHAR_UUID = fromString4;
        UUID fromString5 = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e");
        Intrinsics.checkNotNullExpressionValue(fromString5, "fromString(...)");
        CMD_CHAR_UUID = fromString5;
        UUID fromString6 = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        Intrinsics.checkNotNullExpressionValue(fromString6, "fromString(...)");
        CCCD_UUID = fromString6;
        MutableStateFlow<State> MutableStateFlow = StateFlowKt.MutableStateFlow(State.DISCONNECTED);
        _state = MutableStateFlow;
        state = FlowKt.asStateFlow(MutableStateFlow);
        MutableStateFlow<String> MutableStateFlow2 = StateFlowKt.MutableStateFlow(null);
        _lastSent = MutableStateFlow2;
        lastSent = FlowKt.asStateFlow(MutableStateFlow2);
        adapter = LazyKt.lazy(new Function0<BluetoothAdapter>() { // from class: com.mapsbledisplay.BleManager$adapter$2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final BluetoothAdapter invoke() {
                Context context;
                context = BleManager.appContext;
                if (context == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("appContext");
                    context = null;
                }
                Object systemService = context.getSystemService("bluetooth");
                BluetoothManager bluetoothManager = systemService instanceof BluetoothManager ? (BluetoothManager) systemService : null;
                if (bluetoothManager != null) {
                    return bluetoothManager.getAdapter();
                }
                return null;
            }
        });
        main = new Handler(Looper.getMainLooper());
        scanCallback = new ScanCallback() { // from class: com.mapsbledisplay.BleManager$scanCallback$1
            @Override // android.bluetooth.le.ScanCallback
            public void onScanResult(int callbackType, ScanResult result) {
                boolean z;
                Intrinsics.checkNotNullParameter(result, "result");
                z = BleManager.scanning;
                if (z) {
                    String name = result.getDevice().getName();
                    if (name == null) {
                        ScanRecord scanRecord = result.getScanRecord();
                        name = scanRecord != null ? scanRecord.getDeviceName() : null;
                    }
                    Log.i("BleManager", "Gefunden: " + name + " / " + result.getDevice().getAddress());
                    BleManager.INSTANCE.stopScan();
                    BleManager bleManager = BleManager.INSTANCE;
                    BluetoothDevice device = result.getDevice();
                    Intrinsics.checkNotNullExpressionValue(device, "getDevice(...)");
                    bleManager.connect(device);
                }
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanFailed(int errorCode) {
                MutableStateFlow mutableStateFlow;
                Log.e("BleManager", "Scan fehlgeschlagen: " + errorCode);
                BleManager bleManager = BleManager.INSTANCE;
                BleManager.scanning = false;
                mutableStateFlow = BleManager._state;
                mutableStateFlow.setValue(BleManager.State.DISCONNECTED);
            }
        };
        gattCallback = new BleManager$gattCallback$1();
    }

    public final UUID getSERVICE_UUID() {
        return SERVICE_UUID;
    }

    public final UUID getNAV_CHAR_UUID() {
        return NAV_CHAR_UUID;
    }

    public final UUID getICON_CHAR_UUID() {
        return ICON_CHAR_UUID;
    }

    public final UUID getMEDIA_CHAR_UUID() {
        return MEDIA_CHAR_UUID;
    }

    public final UUID getCMD_CHAR_UUID() {
        return CMD_CHAR_UUID;
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* compiled from: BleManager.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\u0007"}, d2 = {"Lcom/mapsbledisplay/BleManager$State;", "", "(Ljava/lang/String;I)V", "DISCONNECTED", "SCANNING", "CONNECTING", "CONNECTED", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class State {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ State[] $VALUES;
        public static final State DISCONNECTED = new State("DISCONNECTED", 0);
        public static final State SCANNING = new State("SCANNING", 1);
        public static final State CONNECTING = new State("CONNECTING", 2);
        public static final State CONNECTED = new State("CONNECTED", 3);

        private static final /* synthetic */ State[] $values() {
            return new State[]{DISCONNECTED, SCANNING, CONNECTING, CONNECTED};
        }

        public static EnumEntries<State> getEntries() {
            return $ENTRIES;
        }

        public static State valueOf(String str) {
            return (State) Enum.valueOf(State.class, str);
        }

        public static State[] values() {
            return (State[]) $VALUES.clone();
        }

        static {
            State[] $values = $values();
            $VALUES = $values;
            $ENTRIES = EnumEntriesKt.enumEntries($values);
        }

        private State(String str, int i) {
        }
    }

    public final StateFlow<State> getState() {
        return state;
    }

    public final StateFlow<String> getLastSent() {
        return lastSent;
    }

    private final BluetoothAdapter getAdapter() {
        return (BluetoothAdapter) adapter.getValue();
    }

    public final void init(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (appContext == null) {
            Context applicationContext = context.getApplicationContext();
            Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
            appContext = applicationContext;
        }
    }

    public final boolean isBluetoothOn() {
        BluetoothAdapter adapter2 = getAdapter();
        return adapter2 != null && adapter2.isEnabled();
    }

    public final void startScanAndConnect() {
        MutableStateFlow<State> mutableStateFlow = _state;
        if (mutableStateFlow.getValue() == State.CONNECTED || scanning) {
            return;
        }
        BluetoothAdapter adapter2 = getAdapter();
        BluetoothLeScanner bluetoothLeScanner = adapter2 != null ? adapter2.getBluetoothLeScanner() : null;
        if (bluetoothLeScanner == null) {
            Log.w(TAG, "Kein BluetoothLeScanner (Bluetooth aus?)");
            return;
        }
        ScanFilter build = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(SERVICE_UUID)).build();
        ScanSettings build2 = new ScanSettings.Builder().setScanMode(2).build();
        scanning = true;
        mutableStateFlow.setValue(State.SCANNING);
        bluetoothLeScanner.startScan(CollectionsKt.listOf(build), build2, scanCallback);
        Log.i(TAG, "Scan gestartet");
        main.postDelayed(new Runnable() { // from class: com.mapsbledisplay.BleManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BleManager.startScanAndConnect$lambda$1();
            }
        }, SCAN_TIMEOUT_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startScanAndConnect$lambda$1() {
        if (scanning) {
            INSTANCE.stopScan();
            MutableStateFlow<State> mutableStateFlow = _state;
            if (mutableStateFlow.getValue() == State.SCANNING) {
                mutableStateFlow.setValue(State.DISCONNECTED);
            }
            Log.i(TAG, "Scan-Timeout, nichts gefunden");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void stopScan() {
        BluetoothLeScanner bluetoothLeScanner;
        if (scanning) {
            scanning = false;
            try {
                BluetoothAdapter adapter2 = getAdapter();
                if (adapter2 == null || (bluetoothLeScanner = adapter2.getBluetoothLeScanner()) == null) {
                    return;
                }
                bluetoothLeScanner.stopScan(scanCallback);
            } catch (Exception e) {
                Log.w(TAG, "stopScan: " + e.getMessage());
            }
        }
    }

    public final void connectTo(String address) {
        Intrinsics.checkNotNullParameter(address, "address");
        if (_state.getValue() != State.DISCONNECTED) {
            return;
        }
        BluetoothDevice bluetoothDevice = null;
        try {
            BluetoothAdapter adapter2 = getAdapter();
            if (adapter2 != null) {
                bluetoothDevice = adapter2.getRemoteDevice(address);
            }
        } catch (IllegalArgumentException unused) {
            Log.w(TAG, "Ungueltige Adresse " + address);
        }
        if (bluetoothDevice == null) {
            return;
        }
        stopScan();
        Log.i(TAG, "Direktverbindung zu " + address);
        connect(bluetoothDevice);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void connect(BluetoothDevice device) {
        if (gatt == null) {
            MutableStateFlow<State> mutableStateFlow = _state;
            if (mutableStateFlow.getValue() != State.CONNECTING && mutableStateFlow.getValue() != State.CONNECTED) {
                mutableStateFlow.setValue(State.CONNECTING);
                Context context = appContext;
                if (context == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("appContext");
                    context = null;
                }
                gatt = device.connectGatt(context, false, gattCallback, 2);
                return;
            }
        }
        Log.i(TAG, "Verbindung laeuft bereits - zweiter Versuch ignoriert");
    }

    public final void disconnect() {
        stopScan();
        navChar = null;
        iconChar = null;
        mediaChar = null;
        cmdChar = null;
        writeInFlight = false;
        pendingPayload = null;
        pendingIcon = null;
        pendingMedia = null;
        BluetoothGatt bluetoothGatt = gatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        gatt = null;
        _state.setValue(State.DISCONNECTED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean subscribeCmd(BluetoothGatt g) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = cmdChar;
        if (bluetoothGattCharacteristic == null) {
            return false;
        }
        boolean z = true;
        g.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(CCCD_UUID);
        if (descriptor == null) {
            return false;
        }
        writeInFlight = true;
        if (Build.VERSION.SDK_INT >= 33) {
            if (g.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) != 0) {
                z = false;
            }
        } else {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            z = g.writeDescriptor(descriptor);
        }
        if (!z) {
            writeInFlight = false;
        }
        return z;
    }

    public final void send(NavData nav) {
        Intrinsics.checkNotNullParameter(nav, "nav");
        sendRaw(nav.toPayload());
    }

    public final void sendRaw(String payload) {
        Intrinsics.checkNotNullParameter(payload, "payload");
        pendingPayload = payload;
        lastPayload = payload;
        if (_state.getValue() == State.CONNECTED) {
            flush();
        }
    }

    public final void sendIcon(byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        pendingIcon = data;
        lastIcon = data;
        if (_state.getValue() == State.CONNECTED) {
            flush();
        }
    }

    public final void sendMedia(String payload) {
        Intrinsics.checkNotNullParameter(payload, "payload");
        pendingMedia = payload;
        lastMedia = payload;
        if (_state.getValue() == State.CONNECTED) {
            flush();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void flush() {
        BluetoothGatt bluetoothGatt;
        if (writeInFlight || (bluetoothGatt = gatt) == null) {
            return;
        }
        String str = pendingPayload;
        if (str != null) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = navChar;
            if (bluetoothGattCharacteristic == null) {
                return;
            }
            BleManager bleManager = INSTANCE;
            pendingPayload = null;
            byte[] bytes = str.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            if (bleManager.writeChar(bluetoothGatt, bluetoothGattCharacteristic, bytes)) {
                _lastSent.setValue(str);
                return;
            }
            return;
        }
        byte[] bArr = pendingIcon;
        if (bArr != null) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic2 = iconChar;
            if (bluetoothGattCharacteristic2 == null) {
                pendingIcon = null;
                return;
            }
            BleManager bleManager2 = INSTANCE;
            pendingIcon = null;
            if (bleManager2.writeChar(bluetoothGatt, bluetoothGattCharacteristic2, bArr)) {
                return;
            }
        }
        String str2 = pendingMedia;
        if (str2 != null) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic3 = mediaChar;
            if (bluetoothGattCharacteristic3 == null) {
                pendingMedia = null;
                return;
            }
            BleManager bleManager3 = INSTANCE;
            pendingMedia = null;
            byte[] bytes2 = str2.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            bleManager3.writeChar(bluetoothGatt, bluetoothGattCharacteristic3, bytes2);
        }
    }

    private final boolean writeChar(BluetoothGatt g, BluetoothGattCharacteristic ch, byte[] bytes) {
        boolean z = true;
        writeInFlight = true;
        if (Build.VERSION.SDK_INT >= 33) {
            if (g.writeCharacteristic(ch, bytes, 2) != 0) {
                z = false;
            }
        } else {
            ch.setWriteType(2);
            ch.setValue(bytes);
            z = g.writeCharacteristic(ch);
        }
        if (!z) {
            writeInFlight = false;
            Log.w(TAG, "writeCharacteristic abgelehnt");
        }
        return z;
    }
}
