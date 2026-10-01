package com.mapsbledisplay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import com.mapsbledisplay.BleManager;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;

/* compiled from: DeviceService.kt */
@Metadata(d1 = {"\u0000]\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t*\u0001\u000b\u0018\u0000 &2\u00020\u0001:\u0001&B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0004H\u0002J\u0014\u0010\u0018\u001a\u0004\u0018\u00010\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0016J\u0010\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020\u0016H\u0016J\b\u0010 \u001a\u00020\u0016H\u0016J\"\u0010!\u001a\u00020\u00142\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\"\u001a\u00020\u00142\u0006\u0010#\u001a\u00020\u0014H\u0016J\b\u0010$\u001a\u00020\u0016H\u0002J\u0010\u0010%\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006'"}, d2 = {"Lcom/mapsbledisplay/DeviceService;", "Landroid/app/Service;", "()V", "foregroundOk", "", "graceStop", "Ljava/lang/Runnable;", "handler", "Landroid/os/Handler;", "rebindRetry", "rebindWatchdog", "com/mapsbledisplay/DeviceService$rebindWatchdog$1", "Lcom/mapsbledisplay/DeviceService$rebindWatchdog$1;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "stateJob", "Lkotlinx/coroutines/Job;", "buildNotification", "Landroid/app/Notification;", "textRes", "", "createChannel", "", "listenerBound", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onBleState", "st", "Lcom/mapsbledisplay/BleManager$State;", "onCreate", "onDestroy", "onStartCommand", "flags", "startId", "requestRebind", "updateNotification", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class DeviceService extends Service {
    public static final String ACTION_STOP = "com.mapsbledisplay.STOP";
    private static final String CHANNEL_ID = "keepalive";

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final String EXTRA_ADDRESS = "address";
    private static final long GRACE_MS = 180000;
    private static final int NOTIF_ID = 1;
    private static final long REBIND_INTERVAL_MS = 300000;
    private static final long REBIND_RETRY_MS = 15000;
    private static final String TAG = "DeviceService";
    private boolean foregroundOk;
    private Job stateJob;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final CoroutineScope scope = CoroutineScopeKt.CoroutineScope(SupervisorKt.SupervisorJob$default((Job) null, 1, (Object) null).plus(Dispatchers.getMain()));
    private final DeviceService$rebindWatchdog$1 rebindWatchdog = new Runnable() { // from class: com.mapsbledisplay.DeviceService$rebindWatchdog$1
        @Override // java.lang.Runnable
        public void run() {
            Handler handler;
            ListenerRebind.INSTANCE.heal(DeviceService.this);
            handler = DeviceService.this.handler;
            handler.postDelayed(this, 300000L);
        }
    };
    private final Runnable rebindRetry = new Runnable() { // from class: com.mapsbledisplay.DeviceService$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            DeviceService.rebindRetry$lambda$0(DeviceService.this);
        }
    };
    private final Runnable graceStop = new Runnable() { // from class: com.mapsbledisplay.DeviceService$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            DeviceService.graceStop$lambda$1(DeviceService.this);
        }
    };

    /* compiled from: DeviceService.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[BleManager.State.values().length];
            try {
                iArr[BleManager.State.CONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[BleManager.State.DISCONNECTED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[BleManager.State.SCANNING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[BleManager.State.CONNECTING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* compiled from: DeviceService.kt */
    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001a\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/mapsbledisplay/DeviceService$Companion;", "", "()V", "ACTION_STOP", "", "CHANNEL_ID", "EXTRA_ADDRESS", "GRACE_MS", "", "NOTIF_ID", "", "REBIND_INTERVAL_MS", "REBIND_RETRY_MS", "TAG", "start", "", "context", "Landroid/content/Context;", DeviceService.EXTRA_ADDRESS, "stop", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ void start$default(Companion companion, Context context, String str, int i, Object obj) {
            if ((i & 2) != 0) {
                str = null;
            }
            companion.start(context, str);
        }

        public final void start(Context context, String address) {
            Intrinsics.checkNotNullParameter(context, "context");
            if (!Permissions.INSTANCE.hasBle(context)) {
                Log.w(DeviceService.TAG, "Bluetooth-Berechtigung fehlt - Start uebersprungen");
                return;
            }
            if (CompanionPairing.INSTANCE.getInProgress()) {
                Log.i(DeviceService.TAG, "Kopplung laeuft - Start uebersprungen");
                return;
            }
            Intent intent = new Intent(context, (Class<?>) DeviceService.class);
            if (address != null) {
                intent.putExtra(DeviceService.EXTRA_ADDRESS, address);
            }
            try {
                ContextCompat.startForegroundService(context, intent);
            } catch (Exception e) {
                Log.w(DeviceService.TAG, "Start nicht moeglich: " + e.getMessage());
            }
        }

        public final void stop(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            BackgroundScan.INSTANCE.pause(context);
            BleManager.INSTANCE.disconnect();
            context.stopService(new Intent(context, (Class<?>) DeviceService.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void rebindRetry$lambda$0(DeviceService this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        ListenerRebind.INSTANCE.heal(this$0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void graceStop$lambda$1(DeviceService this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (BleManager.INSTANCE.getState().getValue() != BleManager.State.CONNECTED) {
            Log.i(TAG, "Keine Verbindung seit 180s -> Dienst beendet sich");
            this$0.stopSelf();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        Job launch$default;
        super.onCreate();
        BleManager bleManager = BleManager.INSTANCE;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        bleManager.init(applicationContext);
        createChannel();
        try {
            ServiceCompat.startForeground(this, 1, buildNotification(R.string.notif_connecting), Build.VERSION.SDK_INT >= 29 ? 16 : 0);
            this.foregroundOk = true;
            this.handler.postDelayed(this.rebindWatchdog, REBIND_INTERVAL_MS);
            launch$default = BuildersKt__Builders_commonKt.launch$default(this.scope, null, null, new DeviceService$onCreate$1(this, null), 3, null);
            this.stateJob = launch$default;
            Log.i(TAG, "Dienst gestartet");
        } catch (Exception e) {
            Log.w(TAG, "startForeground fehlgeschlagen: " + e.getMessage());
            stopSelf();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.foregroundOk) {
            return 2;
        }
        if (Intrinsics.areEqual(intent != null ? intent.getAction() : null, ACTION_STOP)) {
            stopSelf();
            return 2;
        }
        String stringExtra = intent != null ? intent.getStringExtra(EXTRA_ADDRESS) : null;
        BleManager.State value = BleManager.INSTANCE.getState().getValue();
        if (stringExtra != null && value == BleManager.State.DISCONNECTED) {
            BleManager.INSTANCE.connectTo(stringExtra);
        } else if (stringExtra != null) {
            Log.i(TAG, "Geraet gesehen, aber Zustand " + value + " - ignoriert");
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onBleState(BleManager.State st) {
        int i = WhenMappings.$EnumSwitchMapping$0[st.ordinal()];
        if (i == 1) {
            this.handler.removeCallbacks(this.graceStop);
            BackgroundScan.INSTANCE.ensure(this);
            updateNotification(R.string.notif_connected);
            if (!listenerBound()) {
                requestRebind();
            }
            this.handler.removeCallbacks(this.rebindRetry);
            this.handler.postDelayed(this.rebindRetry, REBIND_RETRY_MS);
            return;
        }
        if (i == 2) {
            updateNotification(R.string.notif_waiting);
            BackgroundScan.INSTANCE.ensure(this);
            this.handler.removeCallbacks(this.graceStop);
            this.handler.postDelayed(this.graceStop, GRACE_MS);
            return;
        }
        if (i == 3 || i == 4) {
            this.handler.removeCallbacks(this.graceStop);
            updateNotification(R.string.notif_connecting);
        }
    }

    private final boolean listenerBound() {
        return MapsNotificationListenerService.INSTANCE.getConnected().getValue().booleanValue();
    }

    private final void requestRebind() {
        try {
            NotificationListenerService.requestRebind(new ComponentName(this, (Class<?>) MapsNotificationListenerService.class));
        } catch (Exception e) {
            Log.w(TAG, "requestRebind: " + e.getMessage());
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.handler.removeCallbacksAndMessages(null);
        Job job = this.stateJob;
        if (job != null) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        }
        CoroutineScopeKt.cancel$default(this.scope, null, 1, null);
        Log.i(TAG, "Dienst beendet");
        super.onDestroy();
    }

    private final Notification buildNotification(int textRes) {
        DeviceService deviceService = this;
        Notification build = new NotificationCompat.Builder(deviceService, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.keepalive_title)).setContentText(getString(textRes)).setOngoing(true).setPriority(-2).setContentIntent(PendingIntent.getActivity(deviceService, 0, new Intent(deviceService, (Class<?>) MainActivity.class), AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL)).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        return build;
    }

    private final void updateNotification(int textRes) {
        if (this.foregroundOk) {
            try {
                ((NotificationManager) getSystemService(NotificationManager.class)).notify(1, buildNotification(textRes));
            } catch (Exception e) {
                Log.w(TAG, "notify: " + e.getMessage());
            }
        }
    }

    private final void createChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.keepalive_channel), 1);
        notificationChannel.setShowBadge(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
