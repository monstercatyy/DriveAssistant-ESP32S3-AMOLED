package com.driveassistant.v2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "DriveAssistantBT";

    // ESP32 GATT Service & Characteristic UUIDs
    public static final UUID SERVICE_UUID    = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID NAV_CHAR_UUID   = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID CMD_CHAR_UUID   = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e");

    private final Context context;
    private final BluetoothAdapter adapter;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic navChar;
    private BluetoothGattCharacteristic cmdChar;
    private boolean isConnected = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface StatusListener {
        void onConnectionStateChange(boolean connected, String deviceName);
        void onLog(String message);
    }

    private StatusListener listener;

    public BluetoothManager(Context context, StatusListener listener) {
        this.context = context;
        this.listener = listener;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isConnected() {
        return isConnected;
    }

    private BluetoothLeScanner bleScanner;
    private final BleScanCallback scanCallback = new BleScanCallback(this);
    private final BleGattCallback gattCallback = new BleGattCallback(this);

    private static class BleScanCallback extends ScanCallback {
        private final BluetoothManager mgr;
        BleScanCallback(BluetoothManager mgr) { this.mgr = mgr; }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice dev = result.getDevice();
            String name = dev.getName();
            if (name != null && (name.contains("Drive Assistant") || name.contains("ESP32"))) {
                if (mgr.bleScanner != null) {
                    try { mgr.bleScanner.stopScan(this); } catch (Exception ignored) {}
                }
                mgr.connectToDevice(dev);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (mgr.listener != null) mgr.listener.onLog("BLE Scan failed: " + errorCode);
        }
    }

    public void startAutoConnect() {
        if (adapter == null || !adapter.isEnabled()) {
            if (listener != null) listener.onLog("Bluetooth is disabled");
            return;
        }

        if (listener != null) listener.onLog("Scanning for Drive Assistant...");
        bleScanner = adapter.getBluetoothLeScanner();
        if (bleScanner == null) return;

        bleScanner.startScan(scanCallback);
    }

    public void connectToDevice(BluetoothDevice device) {
        if (listener != null) listener.onLog("Connecting to " + device.getName() + "...");
        // Direct GATT connection with TRANSPORT_LE
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
    }

    private static class BleGattCallback extends BluetoothGattCallback {
        private final BluetoothManager mgr;
        BleGattCallback(BluetoothManager mgr) { this.mgr = mgr; }

        @Override
        public void onConnectionStateChange(BluetoothGatt g, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mgr.isConnected = true;
                // Request 512 MTU for high-throughput road vector streaming
                g.requestMtu(512);
                mgr.handler.post(() -> {
                    if (mgr.listener != null) mgr.listener.onConnectionStateChange(true, g.getDevice().getName());
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mgr.isConnected = false;
                mgr.navChar = null;
                mgr.cmdChar = null;
                mgr.handler.post(() -> {
                    if (mgr.listener != null) mgr.listener.onConnectionStateChange(false, null);
                });
                // Auto-retry connection
                mgr.handler.postDelayed(() -> mgr.startAutoConnect(), 3000);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt g, int mtu, int status) {
            Log.d(TAG, "GATT MTU negotiated: " + mtu);
            // Request high priority connection interval for lowest latency (<15ms)
            g.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
            g.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt g, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = g.getService(SERVICE_UUID);
                if (service != null) {
                    mgr.navChar = service.getCharacteristic(NAV_CHAR_UUID);
                    mgr.cmdChar = service.getCharacteristic(CMD_CHAR_UUID);
                    Log.d(TAG, "Drive Assistant GATT characteristics ready!");
                }
            }
        }
    }

    public boolean sendNavPacket(String maneuver, String distance, String street, int progressPct) {
        return sendNavPacket(maneuver, distance, street, progressPct, "", "");
    }

    public boolean sendNavPacket(String maneuver, String distance, String street, int progressPct, String eta, String remDist) {
        if (gatt == null || navChar == null || !isConnected) return false;
        String payload = maneuver + "|" + distance + "|" + street + "|" + progressPct + "|" + (eta != null ? eta : "") + "|" + (remDist != null ? remDist : "");
        navChar.setValue(payload.getBytes());
        navChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        return gatt.writeCharacteristic(navChar);
    }

    public boolean sendVectorRoad(String vectorData) {
        if (gatt == null || navChar == null || !isConnected) return false;
        navChar.setValue(vectorData.getBytes());
        navChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        return gatt.writeCharacteristic(navChar);
    }

    public boolean sendWeatherPacket(String cond, String temp) {
        if (gatt == null || navChar == null || !isConnected) return false;
        String payload = "wth|" + cond + "|" + temp;
        navChar.setValue(payload.getBytes());
        navChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        return gatt.writeCharacteristic(navChar);
    }

    public void disconnect() {
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
        isConnected = false;
    }
}
