package com.mapsbledisplay;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ListenerRebind.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u0011\u001a\u00020\u00102\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/mapsbledisplay/ListenerRebind;", "", "()V", "CHECK_DELAY_MS", "", "MIN_INTERVAL_MS", "TAG", "", "handler", "Landroid/os/Handler;", "lastToggle", "accessGranted", "", "context", "Landroid/content/Context;", "heal", "", "healSoon", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class ListenerRebind {
    private static final long CHECK_DELAY_MS = 3000;
    private static final long MIN_INTERVAL_MS = 30000;
    private static final String TAG = "ListenerRebind";
    private static long lastToggle;
    public static final ListenerRebind INSTANCE = new ListenerRebind();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private ListenerRebind() {
    }

    private final boolean accessGranted(Context context) {
        return NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.getPackageName());
    }

    public final void healSoon(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        final Context applicationContext = context.getApplicationContext();
        handler.postDelayed(new Runnable() { // from class: com.mapsbledisplay.ListenerRebind$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ListenerRebind.healSoon$lambda$0(applicationContext);
            }
        }, CHECK_DELAY_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void healSoon$lambda$0(Context context) {
        ListenerRebind listenerRebind = INSTANCE;
        Intrinsics.checkNotNull(context);
        listenerRebind.heal(context);
    }

    public final void heal(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Context applicationContext = context.getApplicationContext();
        Intrinsics.checkNotNull(applicationContext);
        if (!accessGranted(applicationContext) || MapsNotificationListenerService.INSTANCE.getConnected().getValue().booleanValue()) {
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = lastToggle;
        if (j == 0 || elapsedRealtime - j >= MIN_INTERVAL_MS) {
            lastToggle = elapsedRealtime;
            ComponentName componentName = new ComponentName(applicationContext, (Class<?>) MapsNotificationListenerService.class);
            try {
                PackageManager packageManager = applicationContext.getPackageManager();
                packageManager.setComponentEnabledSetting(componentName, 2, 1);
                packageManager.setComponentEnabledSetting(componentName, 0, 1);
                NotificationListenerService.requestRebind(componentName);
                Log.i(TAG, "Listener war nicht gebunden -> Komponente umgeschaltet");
            } catch (Exception e) {
                Log.w(TAG, "Neu binden fehlgeschlagen: " + e.getMessage());
            }
        }
    }
}
