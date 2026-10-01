package com.mapsbledisplay;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* compiled from: MediaWatcher.kt */
@Metadata(d1 = {"\u0000E\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000*\u0001\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\n\u0010\u0014\u001a\u0004\u0018\u00010\nH\u0002J\b\u0010\u0015\u001a\u00020\u0011H\u0002J\b\u0010\u0016\u001a\u00020\u0011H\u0002J\u000e\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0019R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lcom/mapsbledisplay/MediaWatcher;", "", "()V", "TAG", "", "controllerCallback", "com/mapsbledisplay/MediaWatcher$controllerCallback$1", "Lcom/mapsbledisplay/MediaWatcher$controllerCallback$1;", "controllers", "", "Landroid/media/session/MediaController;", "lastSent", "listenerComponent", "Landroid/content/ComponentName;", "sessionManager", "Landroid/media/session/MediaSessionManager;", "handleCommand", "", "cmd", "", "pick", "push", "rebind", "start", "context", "Landroid/content/Context;", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class MediaWatcher {
    private static final String TAG = "MediaWatcher";
    private static String lastSent;
    private static ComponentName listenerComponent;
    private static MediaSessionManager sessionManager;
    public static final MediaWatcher INSTANCE = new MediaWatcher();
    private static List<MediaController> controllers = CollectionsKt.emptyList();
    private static final MediaWatcher$controllerCallback$1 controllerCallback = new MediaController.Callback() { // from class: com.mapsbledisplay.MediaWatcher$controllerCallback$1
        @Override // android.media.session.MediaController.Callback
        public void onMetadataChanged(MediaMetadata metadata) {
            MediaWatcher.INSTANCE.push();
        }

        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState state) {
            MediaWatcher.INSTANCE.push();
        }

        @Override // android.media.session.MediaController.Callback
        public void onSessionDestroyed() {
            MediaWatcher.INSTANCE.rebind();
        }
    };

    private MediaWatcher() {
    }

    public final void start(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (sessionManager == null) {
            Object systemService = context.getSystemService("media_session");
            Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.media.session.MediaSessionManager");
            sessionManager = (MediaSessionManager) systemService;
            listenerComponent = new ComponentName(context.getApplicationContext(), (Class<?>) MapsNotificationListenerService.class);
            try {
                MediaSessionManager mediaSessionManager = sessionManager;
                if (mediaSessionManager != null) {
                    mediaSessionManager.addOnActiveSessionsChangedListener(new MediaSessionManager.OnActiveSessionsChangedListener() { // from class: com.mapsbledisplay.MediaWatcher$$ExternalSyntheticLambda0
                        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
                        public final void onActiveSessionsChanged(List list) {
                            MediaWatcher.start$lambda$0(list);
                        }
                    }, listenerComponent);
                }
            } catch (SecurityException e) {
                Log.w(TAG, "Kein Zugriff auf Media-Sessions: " + e.getMessage());
                sessionManager = null;
                return;
            }
        }
        rebind();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void start$lambda$0(List list) {
        INSTANCE.rebind();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void rebind() {
        MediaSessionManager mediaSessionManager = sessionManager;
        if (mediaSessionManager == null) {
            return;
        }
        try {
            Iterator<T> it = controllers.iterator();
            while (it.hasNext()) {
                ((MediaController) it.next()).unregisterCallback(controllerCallback);
            }
            List<MediaController> activeSessions = mediaSessionManager.getActiveSessions(listenerComponent);
            if (activeSessions == null) {
                activeSessions = CollectionsKt.emptyList();
            }
            controllers = activeSessions;
            Iterator<T> it2 = activeSessions.iterator();
            while (it2.hasNext()) {
                ((MediaController) it2.next()).registerCallback(controllerCallback);
            }
            List<MediaController> list = controllers;
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
            Iterator<T> it3 = list.iterator();
            while (it3.hasNext()) {
                arrayList.add(((MediaController) it3.next()).getPackageName());
            }
            Log.i(TAG, "Aktive Media-Sessions: " + arrayList);
        } catch (SecurityException e) {
            Log.w(TAG, "getActiveSessions verweigert: " + e.getMessage());
            controllers = CollectionsKt.emptyList();
        }
        push();
    }

    private final MediaController pick() {
        Object obj;
        Object obj2;
        Iterator<T> it = controllers.iterator();
        while (true) {
            obj = null;
            if (!it.hasNext()) {
                obj2 = null;
                break;
            }
            obj2 = it.next();
            PlaybackState playbackState = ((MediaController) obj2).getPlaybackState();
            if (playbackState != null && playbackState.getState() == 3) {
                break;
            }
        }
        MediaController mediaController = (MediaController) obj2;
        if (mediaController != null) {
            return mediaController;
        }
        Iterator<T> it2 = controllers.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            Object next = it2.next();
            if (((MediaController) next).getMetadata() != null) {
                obj = next;
                break;
            }
        }
        return (MediaController) obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void push() {
        String str;
        MediaController pick = pick();
        if (pick == null) {
            str = "none||";
        } else {
            MediaMetadata metadata = pick.getMetadata();
            String string = metadata != null ? metadata.getString("android.media.metadata.TITLE") : null;
            if (string == null) {
                string = "";
            }
            String string2 = metadata != null ? metadata.getString("android.media.metadata.ARTIST") : null;
            String obj = StringsKt.trim((CharSequence) StringsKt.substringBefore$default(StringsKt.substringBefore$default(string2 != null ? string2 : "", Typography.bullet, (String) null, 2, (Object) null), Typography.middleDot, (String) null, 2, (Object) null)).toString();
            PlaybackState playbackState = pick.getPlaybackState();
            str = ((playbackState == null || playbackState.getState() != 3) ? "pause" : "play") + "|" + StringsKt.replace$default(NavDataKt.toDisplayAscii(string), '|', '/', false, 4, (Object) null) + "|" + StringsKt.replace$default(NavDataKt.toDisplayAscii(obj), '|', '/', false, 4, (Object) null);
        }
        if (Intrinsics.areEqual(str, lastSent)) {
            return;
        }
        lastSent = str;
        Log.i(TAG, "Sende Media -> " + str);
        BleManager.INSTANCE.sendMedia(str);
    }

    public final void handleCommand(byte cmd) {
        MediaController pick = pick();
        if (pick == null) {
            Log.w(TAG, "Kommando '" + ((char) cmd) + "' - keine Media-Session");
            return;
        }
        MediaController.TransportControls transportControls = pick.getTransportControls();
        Intrinsics.checkNotNullExpressionValue(transportControls, "getTransportControls(...)");
        char c = (char) cmd;
        if (c == 'P') {
            PlaybackState playbackState = pick.getPlaybackState();
            if (playbackState == null || playbackState.getState() != 3) {
                transportControls.play();
            } else {
                transportControls.pause();
            }
        } else if (c == 'N') {
            transportControls.skipToNext();
        } else if (c == 'V') {
            transportControls.skipToPrevious();
        } else {
            Log.w(TAG, "Unbekanntes Kommando: " + ((int) cmd));
        }
        Log.i(TAG, "Kommando '" + c + "' -> " + pick.getPackageName());
    }
}
