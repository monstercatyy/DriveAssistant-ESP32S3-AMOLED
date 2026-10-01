package com.mapsbledisplay;

import android.content.Context;
import android.os.Build;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Permissions.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u0011\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0002\u0010\u000b¨\u0006\r"}, d2 = {"Lcom/mapsbledisplay/Permissions;", "", "()V", "hasBle", "", "context", "Landroid/content/Context;", "needsBackgroundLocation", "requestable", "", "", "()[Ljava/lang/String;", "requiredBle", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class Permissions {
    public static final Permissions INSTANCE = new Permissions();

    private Permissions() {
    }

    public final String[] requiredBle() {
        if (Build.VERSION.SDK_INT >= 31) {
            return new String[]{"android.permission.BLUETOOTH_SCAN", "android.permission.BLUETOOTH_CONNECT"};
        }
        return new String[]{"android.permission.ACCESS_FINE_LOCATION"};
    }

    public final String[] requestable() {
        String[] strArr;
        String[] requiredBle = requiredBle();
        if (Build.VERSION.SDK_INT >= 33) {
            strArr = new String[]{"android.permission.POST_NOTIFICATIONS"};
        } else {
            strArr = new String[0];
        }
        return (String[]) ArraysKt.plus((Object[]) requiredBle, (Object[]) strArr);
    }

    public final boolean hasBle(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        for (String str : requiredBle()) {
            if (ContextCompat.checkSelfPermission(context, str) != 0) {
                return false;
            }
        }
        return true;
    }

    public final boolean needsBackgroundLocation(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        int i = Build.VERSION.SDK_INT;
        return 29 <= i && i < 31 && ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_BACKGROUND_LOCATION") != 0;
    }
}
