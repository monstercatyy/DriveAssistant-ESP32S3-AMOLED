package com.mapsbledisplay;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.os.EnvironmentCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

/* compiled from: MapsNotificationListenerService.kt */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\fH\u0016J\b\u0010\u000e\u001a\u00020\fH\u0016J\u0012\u0010\u000f\u001a\u00020\f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0016J\u0012\u0010\u0012\u001a\u00020\f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0016J\u0010\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lcom/mapsbledisplay/MapsNotificationListenerService;", "Landroid/service/notification/NotificationListenerService;", "()V", "lastIconHash", "", "lastSentPayload", "", "mainHandler", "Landroid/os/Handler;", "navEndRunnable", "Ljava/lang/Runnable;", "onCreate", "", "onListenerConnected", "onListenerDisconnected", "onNotificationPosted", "sbn", "Landroid/service/notification/StatusBarNotification;", "onNotificationRemoved", "sendManeuverIcon", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class MapsNotificationListenerService extends NotificationListenerService {
    public static final boolean DEBUG_DUMP = true;
    private static final String END_PAYLOAD = "end||";
    private static final int ICON_SIZE = 40;
    private static final String MAPS_PKG = "com.google.android.apps.maps";
    public static final String KOMOOT_PKG = "de.komoot.android";
    public static final String OSMAND_PREFIX = "net.osmand";
    private static final long NAV_END_DELAY_MS = 15000;

    public static final boolean isNavPackage(String pkg) {
        if (pkg == null) {
            return false;
        }
        return pkg.equals(MAPS_PKG) || pkg.equals(KOMOOT_PKG) || pkg.startsWith(OSMAND_PREFIX);
    }
    private static final String TAG = "MapsListener";
    private int lastIconHash;
    private String lastSentPayload;
    private String sub50Maneuver;
    private String sub50Street;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final MutableStateFlow<Boolean> connected = StateFlowKt.MutableStateFlow(false);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable navEndRunnable = new Runnable() { // from class: com.mapsbledisplay.MapsNotificationListenerService$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            MapsNotificationListenerService.navEndRunnable$lambda$0(MapsNotificationListenerService.this);
        }
    };
    private int sub50Meters = -1;
    private Runnable sub50Runnable = new Sub50Runnable(this);

    /* compiled from: MapsNotificationListenerService.kt */
    public final class Sub50Runnable implements Runnable {
        private final MapsNotificationListenerService service;

        public Sub50Runnable(MapsNotificationListenerService mapsNotificationListenerService) {
            this.service = mapsNotificationListenerService;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.service.stepSub50();
        }
    }

    private final void collectTextViews(View view, StringBuilder sb) {
        if (view == null) {
            return;
        }
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text != null) {
                sb.append(" ");
                sb.append(text);
                return;
            }
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                collectTextViews(viewGroup.getChildAt(i), sb);
            }
        }
    }

    private final String enrichTitleWithViewDistance(StatusBarNotification statusBarNotification, String str, String str2) {
        String value;
        if (str == null) {
            str = "";
        }
        if (new Regex("(\\d+(?:[.,]\\d+)?)[\\s\\u00a0\\u202f]*(km|m|mi|ft|yd)\\b", RegexOption.IGNORE_CASE).containsMatchIn(str)) {
            return str;
        }
        try {
            Notification notification = statusBarNotification.getNotification();
            RemoteViews remoteViews = notification.bigContentView;
            if (remoteViews == null) {
                remoteViews = notification.contentView;
            }
            if (remoteViews == null) {
                return str;
            }
            Context createPackageContext = createPackageContext(statusBarNotification.getPackageName(), 0);
            createPackageContext.setTheme(createPackageContext.getApplicationInfo().theme);
            View apply = remoteViews.apply(createPackageContext, new FrameLayout(createPackageContext));
            StringBuilder sb = new StringBuilder();
            collectTextViews(apply, sb);
            MatchResult find$default = Regex.find$default(new Regex("\\b([0-4]?\\d)[\\s\\u00a0\\u202f]*(m|ft|yd)\\b", RegexOption.IGNORE_CASE), sb.toString(), 0, 2, null);
            if (find$default == null || (value = find$default.getValue()) == null) {
                return str;
            }
            return value + " " + str;
        } catch (Exception unused) {
            return str;
        }
    }

    private final NavData transformSub50NavData(NavData navData) {
        if (navData.isEmpty()) {
            return navData;
        }
        if (!Intrinsics.areEqual(navData.getDistance(), "< 50 m")) {
            this.mainHandler.removeCallbacks(this.sub50Runnable);
            this.sub50Meters = -1;
            return navData;
        }
        String maneuver = navData.getManeuver();
        String street = navData.getStreet();
        String raw = navData.getRaw();
        if (Intrinsics.areEqual(maneuver, "arrive") || Intrinsics.areEqual(maneuver, EnvironmentCompat.MEDIA_UNKNOWN)) {
            this.mainHandler.removeCallbacks(this.sub50Runnable);
            this.sub50Meters = -1;
            return new NavData("arrived", "0 m", street, raw);
        }
        if (this.sub50Meters < 0 || !Intrinsics.areEqual(street, this.sub50Street) || !Intrinsics.areEqual(maneuver, this.sub50Maneuver)) {
            this.sub50Meters = 40;
            this.sub50Maneuver = maneuver;
            this.sub50Street = street;
            Handler handler = this.mainHandler;
            Runnable runnable = this.sub50Runnable;
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000L);
        }
        int i = this.sub50Meters;
        if (i <= 0) {
            return new NavData(maneuver, "0 m", street, raw);
        }
        return new NavData(maneuver, i + " m", street, raw);
    }

    public final void stepSub50() {
        int i = this.sub50Meters;
        if (i <= 0) {
            return;
        }
        int i2 = i - 10;
        this.sub50Meters = i2;
        String str = this.sub50Maneuver;
        if (str == null) {
            str = "straight";
        }
        String str2 = this.sub50Street;
        if (str2 == null) {
            str2 = "";
        }
        if (i2 <= 0) {
            if (Intrinsics.areEqual(str, "arrive")) {
                str = "arrived";
            }
            NavData navData = new NavData(str, "0 m", str2, "");
            this.lastSentPayload = navData.toPayload();
            BleManager.INSTANCE.send(navData);
            return;
        }
        NavData navData2 = new NavData(str, i2 + " m", str2, "");
        this.lastSentPayload = navData2.toPayload();
        BleManager.INSTANCE.send(navData2);
        this.mainHandler.postDelayed(this.sub50Runnable, 1000L);
    }

    /* compiled from: MapsNotificationListenerService.kt */
    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0011"}, d2 = {"Lcom/mapsbledisplay/MapsNotificationListenerService$Companion;", "", "()V", "DEBUG_DUMP", "", "END_PAYLOAD", "", "ICON_SIZE", "", "MAPS_PKG", "NAV_END_DELAY_MS", "", "TAG", "connected", "Lkotlinx/coroutines/flow/MutableStateFlow;", "getConnected", "()Lkotlinx/coroutines/flow/MutableStateFlow;", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final MutableStateFlow<Boolean> getConnected() {
            return MapsNotificationListenerService.connected;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void navEndRunnable$lambda$0(MapsNotificationListenerService this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        String str = this$0.lastSentPayload;
        if (str == null || Intrinsics.areEqual(str, END_PAYLOAD)) {
            return;
        }
        Log.i(TAG, "Maps-Notification 15s weg -> Navigation beendet");
        this$0.lastSentPayload = END_PAYLOAD;
        this$0.lastIconHash = 0;
        BleManager.INSTANCE.sendRaw(END_PAYLOAD);
        BleManager.INSTANCE.sendIcon(new byte[]{73, 0, 0});
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        BleManager bleManager = BleManager.INSTANCE;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        bleManager.init(applicationContext);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn != null && isNavPackage(sbn.getPackageName())) {
            Bundle bundle = sbn.getNotification().extras;
            CharSequence charSequence = bundle.getCharSequence(NotificationCompat.EXTRA_TITLE);
            String obj = charSequence != null ? charSequence.toString() : null;
            CharSequence charSequence2 = bundle.getCharSequence(NotificationCompat.EXTRA_TEXT);
            String obj2 = charSequence2 != null ? charSequence2.toString() : null;
            CharSequence charSequence3 = bundle.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT);
            String obj3 = charSequence3 != null ? charSequence3.toString() : null;
            CharSequence charSequence4 = bundle.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT);
            String obj4 = charSequence4 != null ? charSequence4.toString() : null;
            Log.d(TAG, "----- Maps-Notification -----");
            Log.d(TAG, "title   = " + obj);
            Log.d(TAG, "text    = " + obj2);
            Log.d(TAG, "subText = " + obj3);
            Log.d(TAG, "bigText = " + obj4);
            NavParser navParser = NavParser.INSTANCE;
            if (obj2 == null) {
                obj2 = obj4;
            }
            NavData transformSub50NavData = transformSub50NavData(navParser.parse(enrichTitleWithViewDistance(sbn, obj, obj4), obj2, null));
            if (transformSub50NavData.isEmpty()) {
                return;
            }
            this.mainHandler.removeCallbacks(this.navEndRunnable);
            String payload = transformSub50NavData.toPayload();
            if (!Intrinsics.areEqual(payload, this.lastSentPayload)) {
                this.lastSentPayload = payload;
                Log.i(TAG, "Sende -> " + payload);
                BleManager.INSTANCE.send(transformSub50NavData);
            }
            sendManeuverIcon(sbn);
        }
    }

    private final void sendManeuverIcon(StatusBarNotification sbn) {
        Icon largeIcon = sbn.getNotification().getLargeIcon();
        if (largeIcon == null) {
            largeIcon = sbn.getNotification().getSmallIcon();
        }
        if (largeIcon == null) {
            Log.d(TAG, "kein Icon in der Benachrichtigung");
            return;
        }
        Drawable loadDrawable = largeIcon.loadDrawable(this);
        if (loadDrawable == null) {
            return;
        }
        Bitmap createBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
        Intrinsics.checkNotNullExpressionValue(createBitmap, "createBitmap(...)");
        loadDrawable.setBounds(0, 0, 40, 40);
        loadDrawable.draw(new Canvas(createBitmap));
        byte[] bArr = new byte[203];
        bArr[0] = 73;
        bArr[1] = 40;
        bArr[2] = 40;
        int i = 0;
        int i2 = 0;
        int i3 = 3;
        for (int i4 = 0; i4 < 40; i4++) {
            for (int i5 = 0; i5 < 40; i5++) {
                int pixel = createBitmap.getPixel(i5, i4);
                i = (i << 1) | ((Color.alpha(pixel) <= 96 || ((Color.red(pixel) + Color.green(pixel)) + Color.blue(pixel)) / 3 <= 175) ? 0 : 1);
                i2++;
                if (i2 == 8) {
                    bArr[i3] = (byte) i;
                    i = 0;
                    i3++;
                    i2 = 0;
                }
            }
        }
        createBitmap.recycle();
        int hashCode = Arrays.hashCode(bArr);
        if (hashCode != this.lastIconHash) {
            this.lastIconHash = hashCode;
            Log.i(TAG, "Sende Manoever-Icon");
            BleManager.INSTANCE.sendIcon(bArr);
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn != null && isNavPackage(sbn.getPackageName())) {
            Log.d(TAG, "Nav-Notification entfernt (Ende-Timer laeuft)");
            String str = this.lastSentPayload;
            if (str == null || Intrinsics.areEqual(str, END_PAYLOAD)) {
                return;
            }
            this.mainHandler.removeCallbacks(this.navEndRunnable);
            this.mainHandler.postDelayed(this.navEndRunnable, NAV_END_DELAY_MS);
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerDisconnected() {
        Log.w(TAG, "NotificationListener getrennt");
        connected.setValue(false);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerConnected() {
        Log.i(TAG, "NotificationListener verbunden");
        connected.setValue(true);
        MapsNotificationListenerService mapsNotificationListenerService = this;
        BackgroundScan.INSTANCE.ensure(mapsNotificationListenerService);
        CompanionPairing.INSTANCE.ensureObserving(mapsNotificationListenerService);
        MediaWatcher.INSTANCE.start(mapsNotificationListenerService);
        try {
            StatusBarNotification[] activeNotifications = getActiveNotifications();
            if (activeNotifications != null) {
                ArrayList arrayList = new ArrayList();
                for (StatusBarNotification statusBarNotification : activeNotifications) {
                    if (isNavPackage(statusBarNotification.getPackageName())) {
                        arrayList.add(statusBarNotification);
                    }
                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    onNotificationPosted((StatusBarNotification) it.next());
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "activeNotifications nicht lesbar: " + e.getMessage());
        }
    }
}
