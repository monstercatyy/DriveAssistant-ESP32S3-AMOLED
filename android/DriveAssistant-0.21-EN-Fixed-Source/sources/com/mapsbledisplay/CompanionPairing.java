package com.mapsbledisplay;

import android.bluetooth.le.ScanFilter;
import android.companion.AssociationInfo;
import android.companion.AssociationRequest;
import android.companion.BluetoothLeDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.IntentSender;
import android.net.MacAddress;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mapsbledisplay.BleManager;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CompanionPairing.kt */
@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0012\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\rH\u0002J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u0011\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u0012\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u00042\u0006\u0010\f\u001a\u00020\rJ8\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\r2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u000f0\u00162\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u000f0\u0016H\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u001e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0019"}, d2 = {"Lcom/mapsbledisplay/CompanionPairing;", "", "()V", "TAG", "", "<set-?>", "", "inProgress", "getInProgress", "()Z", "cdm", "Landroid/companion/CompanionDeviceManager;", "context", "Landroid/content/Context;", "ensureObserving", "", "finish", "isPaired", "isSupported", "pairedAddress", "start", "onDialog", "Lkotlin/Function1;", "Landroid/content/IntentSender;", "onFailed", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class CompanionPairing {
    public static final CompanionPairing INSTANCE = new CompanionPairing();
    private static final String TAG = "CompanionPairing";
    private static volatile boolean inProgress;

    private CompanionPairing() {
    }

    public final boolean getInProgress() {
        return inProgress;
    }

    public final boolean isSupported(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return Build.VERSION.SDK_INT >= 31 && context.getPackageManager().hasSystemFeature("android.software.companion_device_setup");
    }

    private final CompanionDeviceManager cdm(Context context) {
        return (CompanionDeviceManager) context.getSystemService(CompanionDeviceManager.class);
    }

    public final String pairedAddress(Context context) {
        CompanionDeviceManager cdm;
        String str;
        Intrinsics.checkNotNullParameter(context, "context");
        if (!isSupported(context) || (cdm = cdm(context)) == null) {
            return null;
        }
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                List<AssociationInfo> myAssociations = cdm.getMyAssociations();
                Intrinsics.checkNotNullExpressionValue(myAssociations, "getMyAssociations(...)");
                Iterator<T> it = myAssociations.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        str = null;
                        break;
                    }
                    MacAddress deviceMacAddress = ((AssociationInfo) it.next()).getDeviceMacAddress();
                    str = deviceMacAddress != null ? deviceMacAddress.toString() : null;
                    if (str != null) {
                        break;
                    }
                }
            } else {
                List<String> associations = cdm.getAssociations();
                Intrinsics.checkNotNullExpressionValue(associations, "getAssociations(...)");
                str = (String) CollectionsKt.firstOrNull((List) associations);
            }
            if (str == null) {
                return null;
            }
            String upperCase = str.toUpperCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
            return upperCase;
        } catch (Exception e) {
            Log.w(TAG, "Kopplungen nicht lesbar: " + e.getMessage());
            return null;
        }
    }

    public final boolean isPaired(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return pairedAddress(context) != null;
    }

    public final void ensureObserving(Context context) {
        String pairedAddress;
        Intrinsics.checkNotNullParameter(context, "context");
        if (Build.VERSION.SDK_INT >= 31 && (pairedAddress = pairedAddress(context)) != null) {
            try {
                CompanionDeviceManager cdm = cdm(context);
                if (cdm != null) {
                    cdm.startObservingDevicePresence(pairedAddress);
                }
                Log.i(TAG, "System beobachtet Drive Assistant " + pairedAddress);
            } catch (Exception e) {
                Log.w(TAG, "startObservingDevicePresence: " + e.getMessage());
            }
        }
    }

    public final void start(Context context, final Function1<? super IntentSender, Unit> onDialog, final Function1<? super String, Unit> onFailed) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(onDialog, "onDialog");
        Intrinsics.checkNotNullParameter(onFailed, "onFailed");
        CompanionDeviceManager cdm = cdm(context);
        if (cdm == null) {
            onFailed.invoke("CompanionDeviceManager fehlt");
            return;
        }
        inProgress = true;
        if (BleManager.INSTANCE.getState().getValue() != BleManager.State.DISCONNECTED) {
            BleManager.INSTANCE.disconnect();
        }
        BluetoothLeDeviceFilter build = new BluetoothLeDeviceFilter.Builder().setScanFilter(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BleManager.INSTANCE.getSERVICE_UUID())).build()).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        AssociationRequest build2 = new AssociationRequest.Builder().addDeviceFilter(build).setSingleDevice(true).build();
        Intrinsics.checkNotNullExpressionValue(build2, "build(...)");
        CompanionDeviceManager.Callback callback = new CompanionDeviceManager.Callback() { // from class: com.mapsbledisplay.CompanionPairing$start$callback$1
            @Override // android.companion.CompanionDeviceManager.Callback
            @Deprecated(message = "Deprecated in Java")
            public void onDeviceFound(IntentSender intentSender) {
                Intrinsics.checkNotNullParameter(intentSender, "intentSender");
                if (Build.VERSION.SDK_INT < 33) {
                    onDialog.invoke(intentSender);
                }
            }

            @Override // android.companion.CompanionDeviceManager.Callback
            public void onAssociationPending(IntentSender intentSender) {
                Intrinsics.checkNotNullParameter(intentSender, "intentSender");
                onDialog.invoke(intentSender);
            }

            @Override // android.companion.CompanionDeviceManager.Callback
            public void onAssociationCreated(AssociationInfo associationInfo) {
                Intrinsics.checkNotNullParameter(associationInfo, "associationInfo");
                Log.i("CompanionPairing", "Gekoppelt: " + associationInfo.getDeviceMacAddress());
            }

            @Override // android.companion.CompanionDeviceManager.Callback
            public void onFailure(CharSequence error) {
                String str;
                Log.w("CompanionPairing", "Kopplung fehlgeschlagen: " + ((Object) error));
                CompanionPairing companionPairing = CompanionPairing.INSTANCE;
                CompanionPairing.inProgress = false;
                Function1<String, Unit> function1 = onFailed;
                if (error == null || (str = error.toString()) == null) {
                    str = "";
                }
                function1.invoke(str);
            }
        };
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                cdm.associate(build2, context.getMainExecutor(), callback);
            } else {
                cdm.associate(build2, callback, (Handler) null);
            }
            Log.i(TAG, "Kopplung gestartet");
        } catch (Exception e) {
            inProgress = false;
            String message = e.getMessage();
            if (message == null) {
                message = "";
            }
            onFailed.invoke(message);
        }
    }

    public final void finish(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        inProgress = false;
        ensureObserving(context);
    }
}
