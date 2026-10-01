package com.mapsbledisplay;

import android.companion.AssociationInfo;
import android.companion.CompanionDeviceService;
import android.net.MacAddress;
import android.os.Build;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Locale;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DevicePresenceService.kt */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0017J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0017¨\u0006\f"}, d2 = {"Lcom/mapsbledisplay/DevicePresenceService;", "Landroid/companion/CompanionDeviceService;", "()V", "appeared", "", DeviceService.EXTRA_ADDRESS, "", "onDeviceAppeared", "associationInfo", "Landroid/companion/AssociationInfo;", "onDeviceDisappeared", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class DevicePresenceService extends CompanionDeviceService {
    private static final String TAG = "DevicePresence";

    @Override // android.companion.CompanionDeviceService
    @Deprecated(message = "Deprecated in Java")
    public void onDeviceAppeared(String address) {
        Intrinsics.checkNotNullParameter(address, "address");
        if (Build.VERSION.SDK_INT < 33) {
            appeared(address);
        }
    }

    @Override // android.companion.CompanionDeviceService
    public void onDeviceAppeared(AssociationInfo associationInfo) {
        String macAddress;
        Intrinsics.checkNotNullParameter(associationInfo, "associationInfo");
        MacAddress deviceMacAddress = associationInfo.getDeviceMacAddress();
        if (deviceMacAddress == null || (macAddress = deviceMacAddress.toString()) == null) {
            return;
        }
        appeared(macAddress);
    }

    @Override // android.companion.CompanionDeviceService
    @Deprecated(message = "Deprecated in Java")
    public void onDeviceDisappeared(String address) {
        Intrinsics.checkNotNullParameter(address, "address");
        Log.i(TAG, "Drive Assistant nicht mehr sichtbar");
    }

    @Override // android.companion.CompanionDeviceService
    public void onDeviceDisappeared(AssociationInfo associationInfo) {
        Intrinsics.checkNotNullParameter(associationInfo, "associationInfo");
        Log.i(TAG, "Drive Assistant nicht mehr sichtbar");
    }

    private final void appeared(String address) {
        String upperCase = address.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
        DevicePresenceService devicePresenceService = this;
        if (BackgroundScan.INSTANCE.isPaused(devicePresenceService)) {
            Log.i(TAG, "Drive Assistant sichtbar (" + upperCase + "), Auto-Verbinden pausiert");
            return;
        }
        Log.i(TAG, "Drive Assistant sichtbar (" + upperCase + ") -> verbinde");
        DeviceService.INSTANCE.start(devicePresenceService, upperCase);
        ListenerRebind.INSTANCE.healSoon(devicePresenceService);
    }
}
