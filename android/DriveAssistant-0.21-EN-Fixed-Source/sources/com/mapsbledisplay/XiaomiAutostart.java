package com.mapsbledisplay;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: XiaomiAutostart.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\u0006\u0010\f\u001a\u00020\bJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lcom/mapsbledisplay/XiaomiAutostart;", "", "()V", "OP_AUTOSTART", "", "TAG", "", "isAllowed", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)Ljava/lang/Boolean;", "isXiaomi", "openSettings", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class XiaomiAutostart {
    public static final XiaomiAutostart INSTANCE = new XiaomiAutostart();
    private static final int OP_AUTOSTART = 10008;
    private static final String TAG = "XiaomiAutostart";

    private XiaomiAutostart() {
    }

    public final boolean isXiaomi() {
        List<String> listOf = CollectionsKt.listOf((Object[]) new String[]{Build.MANUFACTURER, Build.BRAND});
        if ((listOf instanceof Collection) && listOf.isEmpty()) {
            return false;
        }
        for (String str : listOf) {
            if (StringsKt.equals(str, "xiaomi", true) || StringsKt.equals(str, "redmi", true) || StringsKt.equals(str, "poco", true)) {
                return true;
            }
        }
        return false;
    }

    public final Boolean isAllowed(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (!isXiaomi()) {
            return null;
        }
        try {
            Object invoke = AppOpsManager.class.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class).invoke((AppOpsManager) context.getSystemService(AppOpsManager.class), Integer.valueOf(OP_AUTOSTART), Integer.valueOf(Process.myUid()), context.getPackageName());
            Intrinsics.checkNotNull(invoke, "null cannot be cast to non-null type kotlin.Int");
            return Boolean.valueOf(((Integer) invoke).intValue() == 0);
        } catch (Exception e) {
            Log.w(TAG, "Autostart-Status nicht lesbar: " + e.getMessage());
            return null;
        }
    }

    public final void openSettings(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", context.getPackageName(), null));
        Intent className = new Intent().setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
        Intrinsics.checkNotNullExpressionValue(className, "setClassName(...)");
        Iterator it = CollectionsKt.listOf((Object[]) new Intent[]{className, intent}).iterator();
        while (it.hasNext()) {
            try {
                context.startActivity(((Intent) it.next()).addFlags(268435456));
                return;
            } catch (Exception e) {
                Log.w(TAG, "Einstellungen nicht zu oeffnen: " + e.getMessage());
            }
        }
    }
}
