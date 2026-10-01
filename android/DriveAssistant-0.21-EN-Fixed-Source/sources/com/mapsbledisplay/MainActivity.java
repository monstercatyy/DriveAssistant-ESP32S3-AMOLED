package com.mapsbledisplay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LifecycleOwnerKt;
import com.mapsbledisplay.BleManager;
import com.mapsbledisplay.databinding.ActivityMainBinding;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 +2\u00020\u0001:\u0001+B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0010H\u0002J\b\u0010\u0012\u001a\u00020\u0004H\u0002J\b\u0010\u0013\u001a\u00020\u0004H\u0002J\b\u0010\u0014\u001a\u00020\u0004H\u0002J\b\u0010\u0015\u001a\u00020\u0010H\u0002J\u0012\u0010\u0016\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0014J\b\u0010\u0019\u001a\u00020\u0010H\u0014J\b\u0010\u001a\u001a\u00020\u0010H\u0002J\u0010\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\b\u0010\u001e\u001a\u00020\u0010H\u0002J\b\u0010\u001f\u001a\u00020\u0010H\u0002J\u0013\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070\u000eH\u0002¢\u0006\u0002\u0010!J\u0013\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00070\u000eH\u0002¢\u0006\u0002\u0010!J\u0018\u0010#\u001a\u00020\u00102\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u0004H\u0002J\b\u0010'\u001a\u00020\u0010H\u0002J\b\u0010(\u001a\u00020\u0010H\u0002J\u0010\u0010)\u001a\u00020\u00102\u0006\u0010*\u001a\u00020\u0007H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.¢\u0006\u0002\n\u0000R\u001c\u0010\u000b\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\f0\f0\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R(\u0010\r\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u0007 \b*\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u000e0\u000e0\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006,"}, d2 = {"Lcom/mapsbledisplay/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "autoArmed", "", "bgLocationLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "binding", "Lcom/mapsbledisplay/databinding/ActivityMainBinding;", "pairLauncher", "Landroidx/activity/result/IntentSenderRequest;", "permissionLauncher", "", "armAutoConnect", "", "ensurePermissionsThenScan", "hasAllPermissions", "isNotificationAccessGranted", "needsPairing", "observeState", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "refreshAutostart", "refreshBleStatus", "st", "Lcom/mapsbledisplay/BleManager$State;", "refreshNotifAccess", "refreshPairUi", "requestablePermissions", "()[Ljava/lang/String;", "requiredBlePermissions", "setDot", "dot", "Landroid/widget/TextView;", "ok", "showHelpDialog", "startPairing", "toast", NotificationCompat.CATEGORY_MESSAGE, "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class MainActivity extends AppCompatActivity {
    private static final int DOT_GREEN = -13730510;
    private static final int DOT_RED = -3790808;
    private static boolean pairOffered;
    private boolean autoArmed;
    private final ActivityResultLauncher<String> bgLocationLauncher;
    private ActivityMainBinding binding;
    private final ActivityResultLauncher<IntentSenderRequest> pairLauncher;
    private final ActivityResultLauncher<String[]> permissionLauncher;

    /* compiled from: MainActivity.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[BleManager.State.values().length];
            try {
                iArr[BleManager.State.DISCONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[BleManager.State.SCANNING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[BleManager.State.CONNECTING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[BleManager.State.CONNECTED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public MainActivity() {
        ActivityResultLauncher<String[]> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                MainActivity.permissionLauncher$lambda$0(MainActivity.this, (Map) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResult(...)");
        this.permissionLauncher = registerForActivityResult;
        ActivityResultLauncher<String> registerForActivityResult2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda2
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                MainActivity.bgLocationLauncher$lambda$1(MainActivity.this, (Boolean) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult2, "registerForActivityResult(...)");
        this.bgLocationLauncher = registerForActivityResult2;
        ActivityResultLauncher<IntentSenderRequest> registerForActivityResult3 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda3
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                MainActivity.pairLauncher$lambda$2(MainActivity.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult3, "registerForActivityResult(...)");
        this.pairLauncher = registerForActivityResult3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void permissionLauncher$lambda$0(MainActivity this$0, Map map) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (this$0.hasAllPermissions()) {
            this$0.armAutoConnect();
            if (Permissions.INSTANCE.needsBackgroundLocation(this$0)) {
                this$0.bgLocationLauncher.launch("android.permission.ACCESS_BACKGROUND_LOCATION");
                return;
            }
            return;
        }
        String string = this$0.getString(R.string.perm_denied);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        this$0.toast(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void bgLocationLauncher$lambda$1(MainActivity this$0, Boolean bool) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        BackgroundScan.INSTANCE.start(this$0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void pairLauncher$lambda$2(MainActivity this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        MainActivity mainActivity = this$0;
        CompanionPairing.INSTANCE.finish(mainActivity);
        this$0.refreshPairUi();
        if (activityResult.getResultCode() == -1 && CompanionPairing.INSTANCE.isPaired(mainActivity)) {
            String string = this$0.getString(R.string.pair_done);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            this$0.toast(string);
        }
        this$0.armAutoConnect();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding inflate = ActivityMainBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(...)");
        this.binding = inflate;
        ActivityMainBinding activityMainBinding = null;
        if (inflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            inflate = null;
        }
        ScrollView root = inflate.getRoot();
        DisplaySettingsHelper.attach(this, root);
        setContentView(root);
        BleManager bleManager = BleManager.INSTANCE;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        bleManager.init(applicationContext);
        MainActivity mainActivity = this;
        CompanionPairing.INSTANCE.ensureObserving(mainActivity);
        ActivityMainBinding activityMainBinding2 = this.binding;
        if (activityMainBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding2 = null;
        }
        activityMainBinding2.btnPair.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$3(MainActivity.this, view);
            }
        });
        if (!SetupActivity.INSTANCE.isDone(mainActivity)) {
            pairOffered = true;
            SetupActivity.INSTANCE.start(mainActivity);
        } else if (hasAllPermissions()) {
            this.autoArmed = true;
            armAutoConnect();
        } else {
            this.permissionLauncher.launch(requestablePermissions());
        }
        ActivityMainBinding activityMainBinding3 = this.binding;
        if (activityMainBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding3 = null;
        }
        activityMainBinding3.btnNotifAccess.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$4(MainActivity.this, view);
            }
        });
        ActivityMainBinding activityMainBinding4 = this.binding;
        if (activityMainBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding4 = null;
        }
        activityMainBinding4.btnRestricted.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$5(MainActivity.this, view);
            }
        });
        ActivityMainBinding activityMainBinding5 = this.binding;
        if (activityMainBinding5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding5 = null;
        }
        activityMainBinding5.btnAutostart.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$6(MainActivity.this, view);
            }
        });
        ActivityMainBinding activityMainBinding6 = this.binding;
        if (activityMainBinding6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding6 = null;
        }
        activityMainBinding6.btnConnect.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$7(MainActivity.this, view);
            }
        });
        ActivityMainBinding activityMainBinding7 = this.binding;
        if (activityMainBinding7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding7 = null;
        }
        activityMainBinding7.btnTest.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$8(MainActivity.this, view);
            }
        });
        ActivityMainBinding activityMainBinding8 = this.binding;
        if (activityMainBinding8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            activityMainBinding = activityMainBinding8;
        }
        activityMainBinding.btnMenu.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$11(MainActivity.this, view);
            }
        });
        observeState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$3(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (!BleManager.INSTANCE.isBluetoothOn()) {
            String string = this$0.getString(R.string.bt_off);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            this$0.toast(string);
        } else if (this$0.hasAllPermissions()) {
            this$0.startPairing();
        } else {
            this$0.permissionLauncher.launch(this$0.requestablePermissions());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$4(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        RestrictedSettings.INSTANCE.openNotificationAccess(this$0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$5(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        RestrictedSettings.INSTANCE.openAppInfo(this$0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$6(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        XiaomiAutostart.INSTANCE.openSettings(this$0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$7(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (BleManager.INSTANCE.getState().getValue() == BleManager.State.CONNECTED || BleManager.INSTANCE.getState().getValue() == BleManager.State.CONNECTING) {
            DeviceService.INSTANCE.stop(this$0);
        } else {
            this$0.ensurePermissionsThenScan();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$8(MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        BleManager.INSTANCE.sendRaw("turn-right|200 m|Teststrasse");
        BleManager.INSTANCE.sendIcon(new byte[]{73, 0, 0});
        String string = this$0.getString(R.string.test_sent);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        this$0.toast(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$11(final MainActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        PopupMenu popupMenu = new PopupMenu(this$0, view);
        popupMenu.getMenu().add(0, 1, 0, R.string.menu_setup);
        popupMenu.getMenu().add(0, 2, 1, R.string.menu_help);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: com.mapsbledisplay.MainActivity$$ExternalSyntheticLambda4
            @Override // androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                boolean onCreate$lambda$11$lambda$10$lambda$9;
                onCreate$lambda$11$lambda$10$lambda$9 = MainActivity.onCreate$lambda$11$lambda$10$lambda$9(MainActivity.this, menuItem);
                return onCreate$lambda$11$lambda$10$lambda$9;
            }
        });
        popupMenu.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$11$lambda$10$lambda$9(MainActivity this$0, MenuItem menuItem) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            SetupActivity.INSTANCE.start(this$0);
        } else if (itemId == 2) {
            this$0.showHelpDialog();
        }
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (!this.autoArmed && SetupActivity.INSTANCE.isDone(this) && hasAllPermissions()) {
            this.autoArmed = true;
            armAutoConnect();
        }
        refreshNotifAccess();
        refreshPairUi();
        refreshAutostart();
        ListenerRebind.INSTANCE.healSoon(this);
    }

    private final void refreshAutostart() {
        boolean areEqual = Intrinsics.areEqual((Object) XiaomiAutostart.INSTANCE.isAllowed(this), (Object) false);
        ActivityMainBinding activityMainBinding = this.binding;
        ActivityMainBinding activityMainBinding2 = null;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding = null;
        }
        TextView tvAutostart = activityMainBinding.tvAutostart;
        Intrinsics.checkNotNullExpressionValue(tvAutostart, "tvAutostart");
        tvAutostart.setVisibility(areEqual ? 0 : 8);
        ActivityMainBinding activityMainBinding3 = this.binding;
        if (activityMainBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            activityMainBinding2 = activityMainBinding3;
        }
        Button btnAutostart = activityMainBinding2.btnAutostart;
        Intrinsics.checkNotNullExpressionValue(btnAutostart, "btnAutostart");
        btnAutostart.setVisibility(areEqual ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void armAutoConnect() {
        BackgroundScan.INSTANCE.start(this);
        if (needsPairing() && !pairOffered && BleManager.INSTANCE.isBluetoothOn()) {
            pairOffered = true;
            startPairing();
        } else if (!CompanionPairing.INSTANCE.getInProgress() && BleManager.INSTANCE.isBluetoothOn() && BleManager.INSTANCE.getState().getValue() == BleManager.State.DISCONNECTED) {
            BleManager.INSTANCE.startScanAndConnect();
        }
    }

    private final boolean needsPairing() {
        MainActivity mainActivity = this;
        return CompanionPairing.INSTANCE.isSupported(mainActivity) && !CompanionPairing.INSTANCE.isPaired(mainActivity);
    }

    private final void startPairing() {
        if (Build.VERSION.SDK_INT < 31) {
            return;
        }
        pairOffered = true;
        CompanionPairing.INSTANCE.start(this, new Function1<IntentSender, Unit>() { // from class: com.mapsbledisplay.MainActivity$startPairing$1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(IntentSender intentSender) {
                invoke2(intentSender);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(IntentSender sender) {
                ActivityResultLauncher activityResultLauncher;
                Intrinsics.checkNotNullParameter(sender, "sender");
                activityResultLauncher = MainActivity.this.pairLauncher;
                activityResultLauncher.launch(new IntentSenderRequest.Builder(sender).build());
            }
        }, new Function1<String, Unit>() { // from class: com.mapsbledisplay.MainActivity$startPairing$2
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(String str) {
                invoke2(str);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(String it) {
                Intrinsics.checkNotNullParameter(it, "it");
                CompanionPairing.INSTANCE.finish(MainActivity.this);
                MainActivity mainActivity = MainActivity.this;
                String string = mainActivity.getString(R.string.pair_failed);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                mainActivity.toast(string);
                MainActivity.this.refreshPairUi();
                MainActivity.this.armAutoConnect();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void refreshPairUi() {
        boolean needsPairing = needsPairing();
        ActivityMainBinding activityMainBinding = this.binding;
        ActivityMainBinding activityMainBinding2 = null;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding = null;
        }
        Button btnPair = activityMainBinding.btnPair;
        Intrinsics.checkNotNullExpressionValue(btnPair, "btnPair");
        btnPair.setVisibility(needsPairing ? 0 : 8);
        ActivityMainBinding activityMainBinding3 = this.binding;
        if (activityMainBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            activityMainBinding2 = activityMainBinding3;
        }
        TextView tvPairHint = activityMainBinding2.tvPairHint;
        Intrinsics.checkNotNullExpressionValue(tvPairHint, "tvPairHint");
        tvPairHint.setVisibility(needsPairing ? 0 : 8);
    }

    private final String[] requiredBlePermissions() {
        return Permissions.INSTANCE.requiredBle();
    }

    private final String[] requestablePermissions() {
        String[] strArr;
        String[] requiredBlePermissions = requiredBlePermissions();
        if (Build.VERSION.SDK_INT >= 33) {
            strArr = new String[]{"android.permission.POST_NOTIFICATIONS"};
        } else {
            strArr = new String[0];
        }
        return (String[]) ArraysKt.plus((Object[]) requiredBlePermissions, (Object[]) strArr);
    }

    private final boolean hasAllPermissions() {
        for (String str : requiredBlePermissions()) {
            if (ContextCompat.checkSelfPermission(this, str) != 0) {
                return false;
            }
        }
        return true;
    }

    private final void ensurePermissionsThenScan() {
        if (!BleManager.INSTANCE.isBluetoothOn()) {
            String string = getString(R.string.bt_off);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            toast(string);
        } else if (hasAllPermissions()) {
            armAutoConnect();
        } else {
            this.permissionLauncher.launch(requestablePermissions());
        }
    }

    private final boolean isNotificationAccessGranted() {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0071, code lost:
    
        if (com.mapsbledisplay.RestrictedSettings.INSTANCE.isRestricted(r1) != false) goto L30;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void refreshNotifAccess() {
        /*
            r7 = this;
            boolean r0 = r7.isNotificationAccessGranted()
            com.mapsbledisplay.MapsNotificationListenerService$Companion r1 = com.mapsbledisplay.MapsNotificationListenerService.INSTANCE
            kotlinx.coroutines.flow.MutableStateFlow r1 = r1.getConnected()
            java.lang.Object r1 = r1.getValue()
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            com.mapsbledisplay.databinding.ActivityMainBinding r2 = r7.binding
            r3 = 0
            java.lang.String r4 = "binding"
            if (r2 != 0) goto L1f
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            r2 = r3
        L1f:
            android.widget.TextView r2 = r2.tvNotifStatus
            if (r0 != 0) goto L26
            int r5 = com.mapsbledisplay.R.string.notif_missing
            goto L2d
        L26:
            if (r1 != 0) goto L2b
            int r5 = com.mapsbledisplay.R.string.notif_granted_not_bound
            goto L2d
        L2b:
            int r5 = com.mapsbledisplay.R.string.notif_granted
        L2d:
            java.lang.String r5 = r7.getString(r5)
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            r2.setText(r5)
            com.mapsbledisplay.databinding.ActivityMainBinding r2 = r7.binding
            if (r2 != 0) goto L3e
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            r2 = r3
        L3e:
            android.widget.TextView r2 = r2.dotStep1
            java.lang.String r5 = "dotStep1"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r5)
            r5 = 1
            r6 = 0
            if (r0 == 0) goto L4d
            if (r1 == 0) goto L4d
            r1 = r5
            goto L4e
        L4d:
            r1 = r6
        L4e:
            r7.setDot(r2, r1)
            com.mapsbledisplay.databinding.ActivityMainBinding r1 = r7.binding
            if (r1 != 0) goto L59
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            r1 = r3
        L59:
            android.widget.Button r1 = r1.btnNotifAccess
            r1.setEnabled(r5)
            if (r0 != 0) goto L74
            com.mapsbledisplay.RestrictedSettings r0 = com.mapsbledisplay.RestrictedSettings.INSTANCE
            r1 = r7
            android.content.Context r1 = (android.content.Context) r1
            boolean r0 = r0.tried(r1)
            if (r0 == 0) goto L74
            com.mapsbledisplay.RestrictedSettings r0 = com.mapsbledisplay.RestrictedSettings.INSTANCE
            boolean r0 = r0.isRestricted(r1)
            if (r0 == 0) goto L74
            goto L75
        L74:
            r5 = r6
        L75:
            com.mapsbledisplay.databinding.ActivityMainBinding r0 = r7.binding
            if (r0 != 0) goto L7d
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            r0 = r3
        L7d:
            android.widget.TextView r0 = r0.tvRestricted
            java.lang.String r1 = "tvRestricted"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            android.view.View r0 = (android.view.View) r0
            r1 = 8
            if (r5 == 0) goto L8c
            r2 = r6
            goto L8d
        L8c:
            r2 = r1
        L8d:
            r0.setVisibility(r2)
            if (r5 == 0) goto Lac
            com.mapsbledisplay.databinding.ActivityMainBinding r0 = r7.binding
            if (r0 != 0) goto L9a
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            r0 = r3
        L9a:
            android.widget.TextView r0 = r0.tvRestricted
            com.mapsbledisplay.XiaomiAutostart r2 = com.mapsbledisplay.XiaomiAutostart.INSTANCE
            boolean r2 = r2.isXiaomi()
            if (r2 == 0) goto La7
            int r2 = com.mapsbledisplay.R.string.restricted_hint_xiaomi
            goto La9
        La7:
            int r2 = com.mapsbledisplay.R.string.restricted_hint
        La9:
            r0.setText(r2)
        Lac:
            com.mapsbledisplay.databinding.ActivityMainBinding r0 = r7.binding
            if (r0 != 0) goto Lb4
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            goto Lb5
        Lb4:
            r3 = r0
        Lb5:
            android.widget.Button r0 = r3.btnRestricted
            java.lang.String r2 = "btnRestricted"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r2)
            android.view.View r0 = (android.view.View) r0
            if (r5 == 0) goto Lc1
            goto Lc2
        Lc1:
            r6 = r1
        Lc2:
            r0.setVisibility(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mapsbledisplay.MainActivity.refreshNotifAccess():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void setDot(TextView dot, boolean ok) {
        dot.setTextColor(ok ? DOT_GREEN : DOT_RED);
    }

    private final void showHelpDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.help_title).setMessage(HtmlCompat.fromHtml(getString(R.string.help_text), 0)).setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) null).show();
    }

    private final void observeState() {
        BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new MainActivity$observeState$1(this, null), 3, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void refreshBleStatus(BleManager.State st) {
        int i;
        ActivityMainBinding activityMainBinding = this.binding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityMainBinding = null;
        }
        TextView textView = activityMainBinding.tvBleStatus;
        int i2 = WhenMappings.$EnumSwitchMapping$0[st.ordinal()];
        if (i2 == 1) {
            i = BackgroundScan.INSTANCE.getActive().getValue().booleanValue() ? R.string.ble_auto_armed : R.string.ble_disconnected;
        } else if (i2 == 2) {
            i = R.string.ble_scanning;
        } else if (i2 == 3) {
            i = R.string.ble_connecting;
        } else {
            if (i2 != 4) {
                throw new NoWhenBranchMatchedException();
            }
            i = R.string.ble_connected;
        }
        textView.setText(getString(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void toast(String msg) {
        Toast.makeText(this, msg, 0).show();
    }
}
