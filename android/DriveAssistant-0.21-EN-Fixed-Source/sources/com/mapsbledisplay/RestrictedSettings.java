package com.mapsbledisplay;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RestrictedSettings.kt */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u000e\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lcom/mapsbledisplay/RestrictedSettings;", "", "()V", "KEY_TRIED", "", "OP", "PREFS", "TAG", "isRestricted", "", "context", "Landroid/content/Context;", "openAppInfo", "", "openNotificationAccess", "tried", "wasBlocked", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class RestrictedSettings {
    public static final RestrictedSettings INSTANCE = new RestrictedSettings();
    private static final String KEY_TRIED = "notif_access_tried";
    private static final String OP = "android:access_restricted_settings";
    private static final String PREFS = "setup";
    private static final String TAG = "RestrictedSettings";

    private RestrictedSettings() {
    }

    public final boolean isRestricted(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (Build.VERSION.SDK_INT < 33) {
            return false;
        }
        try {
            int packageSource = context.getPackageManager().getInstallSourceInfo(context.getPackageName()).getPackageSource();
            if (packageSource != 4 && packageSource != 3) {
                return false;
            }
            try {
                return ((AppOpsManager) context.getSystemService(AppOpsManager.class)).unsafeCheckOpNoThrow(OP, Process.myUid(), context.getPackageName()) != 0;
            } catch (Exception e) {
                Log.w(TAG, "Status nicht lesbar: " + e.getMessage());
                return true;
            }
        } catch (Exception unused) {
            return false;
        }
    }

    public final boolean wasBlocked(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (Build.VERSION.SDK_INT < 33) {
            return false;
        }
        try {
            int packageSource = context.getPackageManager().getInstallSourceInfo(context.getPackageName()).getPackageSource();
            return packageSource == 4 || packageSource == 3;
        } catch (Exception unused) {
            return false;
        }
    }

    public final boolean tried(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return context.getSharedPreferences(PREFS, 0).getBoolean(KEY_TRIED, false);
    }

    public final void openNotificationAccess(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        context.getSharedPreferences(PREFS, 0).edit().putBoolean(KEY_TRIED, true).apply();
        if (Build.VERSION.SDK_INT >= 30) {
            Intent putExtra = new Intent("android.settings.NOTIFICATION_LISTENER_DETAIL_SETTINGS").putExtra("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME", new ComponentName(context, (Class<?>) MapsNotificationListenerService.class).flattenToString());
            Intrinsics.checkNotNullExpressionValue(putExtra, "putExtra(...)");
            try {
                context.startActivity(putExtra);
                return;
            } catch (Exception e) {
                Log.w(TAG, "Detailseite nicht verfuegbar: " + e.getMessage());
            }
        }
        context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    public final void openAppInfo(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        context.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", context.getPackageName(), null)));
    }
}
