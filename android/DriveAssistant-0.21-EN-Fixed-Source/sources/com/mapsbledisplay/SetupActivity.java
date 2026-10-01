package com.mapsbledisplay;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import com.mapsbledisplay.BleManager;
import com.mapsbledisplay.databinding.ActivitySetupBinding;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SetupActivity.kt */
@Metadata(d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0005\u0018\u0000 +2\u00020\u0001:\u0002+,B\u0005¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00062\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00140\u0017H\u0002J\b\u0010\u0018\u001a\u00020\u0014H\u0002J\b\u0010\u0019\u001a\u00020\u0014H\u0002J\b\u0010\u001a\u001a\u00020\u0014H\u0002J\u0012\u0010\u001b\u001a\u00020\u00142\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0014J\b\u0010\u001e\u001a\u00020\u0014H\u0014J\u0010\u0010\u001f\u001a\u00020\u00142\u0006\u0010 \u001a\u00020\u001dH\u0014J\b\u0010!\u001a\u00020\u0014H\u0002J\b\u0010\"\u001a\u00020\u0014H\u0002J\b\u0010#\u001a\u00020\u0014H\u0003J'\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u00062\u0006\u0010&\u001a\u00020\u00062\b\u0010'\u001a\u0004\u0018\u00010(H\u0002¢\u0006\u0002\u0010)J\b\u0010*\u001a\u00020\u0014H\u0003R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\t0\t0\bX\u0082\u0004¢\u0006\u0002\n\u0000R(\u0010\u000b\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\r \n*\n\u0012\u0004\u0012\u00020\r\u0018\u00010\f0\f0\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\u000f0\u000f0\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082.¢\u0006\u0002\n\u0000¨\u0006-"}, d2 = {"Lcom/mapsbledisplay/SetupActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "b", "Lcom/mapsbledisplay/databinding/ActivitySetupBinding;", SetupActivity.KEY_INDEX, "", "pairLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroidx/activity/result/IntentSenderRequest;", "kotlin.jvm.PlatformType", "permissionLauncher", "", "", "simpleLauncher", "Landroid/content/Intent;", "steps", "", "Lcom/mapsbledisplay/SetupActivity$Step;", "action", "", "text", "onClick", "Lkotlin/Function0;", "back", "finishSetup", "next", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onSaveInstanceState", "outState", "render", "renderPair", "requestBatteryExemption", "show", "title", "body", "done", "", "(IILjava/lang/Boolean;)V", "startPairing", "Companion", "Step", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class SetupActivity extends AppCompatActivity {
    private static final int COLOR_OK = -13577896;
    private static final int COLOR_OPEN = -3790808;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final String KEY_DONE = "wizard_done";
    private static final String KEY_INDEX = "index";
    private static final String PREFS = "setup";
    private ActivitySetupBinding b;
    private int index;
    private final ActivityResultLauncher<IntentSenderRequest> pairLauncher;
    private final ActivityResultLauncher<String[]> permissionLauncher;
    private final ActivityResultLauncher<Intent> simpleLauncher;
    private List<? extends Step> steps;

    /* compiled from: SetupActivity.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Step.values().length];
            try {
                iArr[Step.WELCOME.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Step.BLUETOOTH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[Step.NOTIF.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[Step.AUTOSTART.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr[Step.PAIR.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr[Step.BATTERY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                iArr[Step.DONE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public SetupActivity() {
        ActivityResultLauncher<String[]> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda5
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                SetupActivity.permissionLauncher$lambda$0(SetupActivity.this, (Map) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResult(...)");
        this.permissionLauncher = registerForActivityResult;
        ActivityResultLauncher<IntentSenderRequest> registerForActivityResult2 = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda6
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                SetupActivity.pairLauncher$lambda$1(SetupActivity.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult2, "registerForActivityResult(...)");
        this.pairLauncher = registerForActivityResult2;
        ActivityResultLauncher<Intent> registerForActivityResult3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda7
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                SetupActivity.simpleLauncher$lambda$2(SetupActivity.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult3, "registerForActivityResult(...)");
        this.simpleLauncher = registerForActivityResult3;
    }

    /* compiled from: SetupActivity.kt */
    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/mapsbledisplay/SetupActivity$Companion;", "", "()V", "COLOR_OK", "", "COLOR_OPEN", "KEY_DONE", "", "KEY_INDEX", "PREFS", "isDone", "", "context", "Landroid/content/Context;", "start", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean isDone(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return context.getSharedPreferences(SetupActivity.PREFS, 0).getBoolean(SetupActivity.KEY_DONE, false);
        }

        public final void start(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            context.startActivity(new Intent(context, (Class<?>) SetupActivity.class));
        }
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* compiled from: SetupActivity.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t¨\u0006\n"}, d2 = {"Lcom/mapsbledisplay/SetupActivity$Step;", "", "(Ljava/lang/String;I)V", "WELCOME", "BLUETOOTH", "NOTIF", "AUTOSTART", "PAIR", "BATTERY", "DONE", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    private static final class Step {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ Step[] $VALUES;
        public static final Step WELCOME = new Step("WELCOME", 0);
        public static final Step BLUETOOTH = new Step("BLUETOOTH", 1);
        public static final Step NOTIF = new Step("NOTIF", 2);
        public static final Step AUTOSTART = new Step("AUTOSTART", 3);
        public static final Step PAIR = new Step("PAIR", 4);
        public static final Step BATTERY = new Step("BATTERY", 5);
        public static final Step DONE = new Step("DONE", 6);

        private static final /* synthetic */ Step[] $values() {
            return new Step[]{WELCOME, BLUETOOTH, NOTIF, AUTOSTART, PAIR, BATTERY, DONE};
        }

        public static EnumEntries<Step> getEntries() {
            return $ENTRIES;
        }

        public static Step valueOf(String str) {
            return (Step) Enum.valueOf(Step.class, str);
        }

        public static Step[] values() {
            return (Step[]) $VALUES.clone();
        }

        static {
            Step[] $values = $values();
            $VALUES = $values;
            $ENTRIES = EnumEntriesKt.enumEntries($values);
        }

        private Step(String str, int i) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void permissionLauncher$lambda$0(SetupActivity this$0, Map map) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        SetupActivity setupActivity = this$0;
        if (Permissions.INSTANCE.hasBle(setupActivity)) {
            BackgroundScan.INSTANCE.start(setupActivity);
        }
        this$0.render();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void pairLauncher$lambda$1(SetupActivity this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        CompanionPairing.INSTANCE.finish(this$0);
        this$0.render();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void simpleLauncher$lambda$2(SetupActivity this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.render();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySetupBinding inflate = ActivitySetupBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(...)");
        this.b = inflate;
        ActivitySetupBinding activitySetupBinding = null;
        if (inflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            inflate = null;
        }
        setContentView(inflate.getRoot());
        BleManager bleManager = BleManager.INSTANCE;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        bleManager.init(applicationContext);
        List createListBuilder = CollectionsKt.createListBuilder();
        createListBuilder.add(Step.WELCOME);
        createListBuilder.add(Step.BLUETOOTH);
        createListBuilder.add(Step.NOTIF);
        if (XiaomiAutostart.INSTANCE.isXiaomi()) {
            createListBuilder.add(Step.AUTOSTART);
        }
        createListBuilder.add(Step.PAIR);
        createListBuilder.add(Step.BATTERY);
        createListBuilder.add(Step.DONE);
        this.steps = CollectionsKt.build(createListBuilder);
        this.index = savedInstanceState != null ? savedInstanceState.getInt(KEY_INDEX) : 0;
        ActivitySetupBinding activitySetupBinding2 = this.b;
        if (activitySetupBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding2 = null;
        }
        activitySetupBinding2.btnNext.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SetupActivity.onCreate$lambda$4(SetupActivity.this, view);
            }
        });
        ActivitySetupBinding activitySetupBinding3 = this.b;
        if (activitySetupBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding3 = null;
        }
        activitySetupBinding3.btnSkip.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SetupActivity.onCreate$lambda$5(SetupActivity.this, view);
            }
        });
        ActivitySetupBinding activitySetupBinding4 = this.b;
        if (activitySetupBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
        } else {
            activitySetupBinding = activitySetupBinding4;
        }
        activitySetupBinding.btnBack.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SetupActivity.onCreate$lambda$6(SetupActivity.this, view);
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback() { // from class: com.mapsbledisplay.SetupActivity$onCreate$5
            {
                super(true);
            }

            @Override // androidx.activity.OnBackPressedCallback
            public void handleOnBackPressed() {
                SetupActivity.this.back();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$4(SetupActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.next();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$5(SetupActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.next();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$6(SetupActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.back();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        render();
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        Intrinsics.checkNotNullParameter(outState, "outState");
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, this.index);
    }

    private final void next() {
        int i = this.index;
        List<? extends Step> list = this.steps;
        if (list == null) {
            Intrinsics.throwUninitializedPropertyAccessException("steps");
            list = null;
        }
        if (i < CollectionsKt.getLastIndex(list)) {
            this.index++;
            render();
        } else {
            finishSetup();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void back() {
        int i = this.index;
        if (i > 0) {
            this.index = i - 1;
            render();
        } else {
            finishSetup();
        }
    }

    private final void finishSetup() {
        getSharedPreferences(PREFS, 0).edit().putBoolean(KEY_DONE, true).apply();
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void render() {
        int i;
        List<? extends Step> list = this.steps;
        ActivitySetupBinding activitySetupBinding = null;
        if (list == null) {
            Intrinsics.throwUninitializedPropertyAccessException("steps");
            list = null;
        }
        Step step = list.get(this.index);
        ActivitySetupBinding activitySetupBinding2 = this.b;
        if (activitySetupBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding2 = null;
        }
        TextView textView = activitySetupBinding2.tvProgress;
        int i2 = R.string.setup_progress;
        Integer valueOf = Integer.valueOf(this.index + 1);
        List<? extends Step> list2 = this.steps;
        if (list2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("steps");
            list2 = null;
        }
        textView.setText(getString(i2, new Object[]{valueOf, Integer.valueOf(list2.size())}));
        ActivitySetupBinding activitySetupBinding3 = this.b;
        if (activitySetupBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding3 = null;
        }
        Button btnBack = activitySetupBinding3.btnBack;
        Intrinsics.checkNotNullExpressionValue(btnBack, "btnBack");
        btnBack.setVisibility(this.index > 0 ? 0 : 8);
        ActivitySetupBinding activitySetupBinding4 = this.b;
        if (activitySetupBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding4 = null;
        }
        TextView tvExtra = activitySetupBinding4.tvExtra;
        Intrinsics.checkNotNullExpressionValue(tvExtra, "tvExtra");
        tvExtra.setVisibility(8);
        ActivitySetupBinding activitySetupBinding5 = this.b;
        if (activitySetupBinding5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding5 = null;
        }
        activitySetupBinding5.tvExtra.setTextColor(COLOR_OPEN);
        ActivitySetupBinding activitySetupBinding6 = this.b;
        if (activitySetupBinding6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding6 = null;
        }
        Button btnExtra = activitySetupBinding6.btnExtra;
        Intrinsics.checkNotNullExpressionValue(btnExtra, "btnExtra");
        btnExtra.setVisibility(8);
        ActivitySetupBinding activitySetupBinding7 = this.b;
        if (activitySetupBinding7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding7 = null;
        }
        Button btnAction = activitySetupBinding7.btnAction;
        Intrinsics.checkNotNullExpressionValue(btnAction, "btnAction");
        btnAction.setVisibility(0);
        switch (WhenMappings.$EnumSwitchMapping$0[step.ordinal()]) {
            case 1:
                show(R.string.setup_welcome_title, R.string.setup_welcome_body, null);
                ActivitySetupBinding activitySetupBinding8 = this.b;
                if (activitySetupBinding8 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("b");
                    activitySetupBinding8 = null;
                }
                Button btnAction2 = activitySetupBinding8.btnAction;
                Intrinsics.checkNotNullExpressionValue(btnAction2, "btnAction");
                btnAction2.setVisibility(8);
                ActivitySetupBinding activitySetupBinding9 = this.b;
                if (activitySetupBinding9 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("b");
                } else {
                    activitySetupBinding = activitySetupBinding9;
                }
                activitySetupBinding.btnNext.setText(R.string.setup_start);
                break;
            case 2:
                show(R.string.setup_bt_title, R.string.setup_bt_body, Boolean.valueOf(Permissions.INSTANCE.hasBle(this)));
                action(R.string.setup_bt_action, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$1
                    {
                        super(0);
                    }

                    @Override // kotlin.jvm.functions.Function0
                    public /* bridge */ /* synthetic */ Unit invoke() {
                        invoke2();
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2() {
                        ActivityResultLauncher activityResultLauncher;
                        activityResultLauncher = SetupActivity.this.permissionLauncher;
                        activityResultLauncher.launch(Permissions.INSTANCE.requestable());
                    }
                });
                break;
            case 3:
                SetupActivity setupActivity = this;
                boolean contains = NotificationManagerCompat.getEnabledListenerPackages(setupActivity).contains(getPackageName());
                boolean isRestricted = RestrictedSettings.INSTANCE.isRestricted(setupActivity);
                boolean wasBlocked = RestrictedSettings.INSTANCE.wasBlocked(setupActivity);
                int i3 = R.string.setup_notif_title;
                if (!contains && (isRestricted || wasBlocked)) {
                    i = R.string.setup_notif_restricted_body;
                } else {
                    i = R.string.setup_notif_body;
                }
                show(i3, i, Boolean.valueOf(contains));
                if (contains) {
                    action(R.string.btn_notif_access, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$2
                        {
                            super(0);
                        }

                        @Override // kotlin.jvm.functions.Function0
                        public /* bridge */ /* synthetic */ Unit invoke() {
                            invoke2();
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2() {
                            RestrictedSettings.INSTANCE.openNotificationAccess(SetupActivity.this);
                        }
                    });
                    break;
                } else if (isRestricted && !RestrictedSettings.INSTANCE.tried(setupActivity)) {
                    action(R.string.setup_notif_try, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$3
                        {
                            super(0);
                        }

                        @Override // kotlin.jvm.functions.Function0
                        public /* bridge */ /* synthetic */ Unit invoke() {
                            invoke2();
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2() {
                            RestrictedSettings.INSTANCE.openNotificationAccess(SetupActivity.this);
                        }
                    });
                    break;
                } else if (isRestricted) {
                    action(R.string.btn_restricted, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$4
                        {
                            super(0);
                        }

                        @Override // kotlin.jvm.functions.Function0
                        public /* bridge */ /* synthetic */ Unit invoke() {
                            invoke2();
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2() {
                            RestrictedSettings.INSTANCE.openAppInfo(SetupActivity.this);
                        }
                    });
                    ActivitySetupBinding activitySetupBinding10 = this.b;
                    if (activitySetupBinding10 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("b");
                        activitySetupBinding10 = null;
                    }
                    activitySetupBinding10.tvExtra.setText(XiaomiAutostart.INSTANCE.isXiaomi() ? R.string.setup_unlock_xiaomi : R.string.setup_unlock_other);
                    ActivitySetupBinding activitySetupBinding11 = this.b;
                    if (activitySetupBinding11 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("b");
                        activitySetupBinding11 = null;
                    }
                    TextView tvExtra2 = activitySetupBinding11.tvExtra;
                    Intrinsics.checkNotNullExpressionValue(tvExtra2, "tvExtra");
                    tvExtra2.setVisibility(0);
                    ActivitySetupBinding activitySetupBinding12 = this.b;
                    if (activitySetupBinding12 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("b");
                        activitySetupBinding12 = null;
                    }
                    activitySetupBinding12.btnExtra.setText(R.string.setup_notif_try_again);
                    ActivitySetupBinding activitySetupBinding13 = this.b;
                    if (activitySetupBinding13 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("b");
                        activitySetupBinding13 = null;
                    }
                    activitySetupBinding13.btnExtra.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda4
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            SetupActivity.render$lambda$7(SetupActivity.this, view);
                        }
                    });
                    ActivitySetupBinding activitySetupBinding14 = this.b;
                    if (activitySetupBinding14 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("b");
                    } else {
                        activitySetupBinding = activitySetupBinding14;
                    }
                    Button btnExtra2 = activitySetupBinding.btnExtra;
                    Intrinsics.checkNotNullExpressionValue(btnExtra2, "btnExtra");
                    btnExtra2.setVisibility(0);
                    break;
                } else {
                    action(R.string.btn_notif_access, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$6
                        {
                            super(0);
                        }

                        @Override // kotlin.jvm.functions.Function0
                        public /* bridge */ /* synthetic */ Unit invoke() {
                            invoke2();
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2() {
                            RestrictedSettings.INSTANCE.openNotificationAccess(SetupActivity.this);
                        }
                    });
                    if (wasBlocked) {
                        ActivitySetupBinding activitySetupBinding15 = this.b;
                        if (activitySetupBinding15 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("b");
                            activitySetupBinding15 = null;
                        }
                        activitySetupBinding15.tvExtra.setText(R.string.setup_unlock_done);
                        ActivitySetupBinding activitySetupBinding16 = this.b;
                        if (activitySetupBinding16 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("b");
                            activitySetupBinding16 = null;
                        }
                        activitySetupBinding16.tvExtra.setTextColor(COLOR_OK);
                        ActivitySetupBinding activitySetupBinding17 = this.b;
                        if (activitySetupBinding17 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("b");
                        } else {
                            activitySetupBinding = activitySetupBinding17;
                        }
                        TextView tvExtra3 = activitySetupBinding.tvExtra;
                        Intrinsics.checkNotNullExpressionValue(tvExtra3, "tvExtra");
                        tvExtra3.setVisibility(0);
                        break;
                    }
                }
                break;
            case 4:
                show(R.string.setup_autostart_title, R.string.setup_autostart_body, Boolean.valueOf(!Intrinsics.areEqual((Object) XiaomiAutostart.INSTANCE.isAllowed(this), (Object) false)));
                action(R.string.btn_autostart, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$7
                    {
                        super(0);
                    }

                    @Override // kotlin.jvm.functions.Function0
                    public /* bridge */ /* synthetic */ Unit invoke() {
                        invoke2();
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2() {
                        XiaomiAutostart.INSTANCE.openSettings(SetupActivity.this);
                    }
                });
                break;
            case 5:
                renderPair();
                break;
            case 6:
                show(R.string.setup_battery_title, XiaomiAutostart.INSTANCE.isXiaomi() ? R.string.setup_battery_body_xiaomi : R.string.setup_battery_body, Boolean.valueOf(((PowerManager) getSystemService(PowerManager.class)).isIgnoringBatteryOptimizations(getPackageName())));
                action(R.string.setup_battery_action, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$8
                    {
                        super(0);
                    }

                    @Override // kotlin.jvm.functions.Function0
                    public /* bridge */ /* synthetic */ Unit invoke() {
                        invoke2();
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2() {
                        SetupActivity.this.requestBatteryExemption();
                    }
                });
                break;
            case 7:
                show(R.string.setup_done_title, R.string.setup_done_body, null);
                boolean z = BleManager.INSTANCE.getState().getValue() == BleManager.State.CONNECTED;
                ActivitySetupBinding activitySetupBinding18 = this.b;
                if (activitySetupBinding18 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("b");
                    activitySetupBinding18 = null;
                }
                Button btnAction3 = activitySetupBinding18.btnAction;
                Intrinsics.checkNotNullExpressionValue(btnAction3, "btnAction");
                btnAction3.setVisibility(z ? 0 : 8);
                action(R.string.btn_test, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$render$9
                    {
                        super(0);
                    }

                    @Override // kotlin.jvm.functions.Function0
                    public /* bridge */ /* synthetic */ Unit invoke() {
                        invoke2();
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2() {
                        BleManager.INSTANCE.sendRaw("turn-right|200 m|Teststrasse");
                        BleManager.INSTANCE.sendIcon(new byte[]{73, 0, 0});
                        Toast.makeText(SetupActivity.this, R.string.test_sent, 0).show();
                    }
                });
                ActivitySetupBinding activitySetupBinding19 = this.b;
                if (activitySetupBinding19 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("b");
                } else {
                    activitySetupBinding = activitySetupBinding19;
                }
                activitySetupBinding.btnNext.setText(R.string.setup_finish);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void render$lambda$7(SetupActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        RestrictedSettings.INSTANCE.openNotificationAccess(this$0);
    }

    private final void renderPair() {
        SetupActivity setupActivity = this;
        if (!CompanionPairing.INSTANCE.isSupported(setupActivity)) {
            show(R.string.setup_pair_title, R.string.setup_connect_body, Boolean.valueOf(BleManager.INSTANCE.getState().getValue() == BleManager.State.CONNECTED));
            action(R.string.btn_connect, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$renderPair$1
                {
                    super(0);
                }

                @Override // kotlin.jvm.functions.Function0
                public /* bridge */ /* synthetic */ Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2() {
                    if (BleManager.INSTANCE.isBluetoothOn()) {
                        BleManager.INSTANCE.startScanAndConnect();
                    } else {
                        Toast.makeText(SetupActivity.this, R.string.bt_off, 0).show();
                    }
                }
            });
        } else {
            show(R.string.setup_pair_title, R.string.setup_pair_body, Boolean.valueOf(CompanionPairing.INSTANCE.isPaired(setupActivity)));
            action(R.string.setup_pair_action, new Function0<Unit>() { // from class: com.mapsbledisplay.SetupActivity$renderPair$2
                {
                    super(0);
                }

                @Override // kotlin.jvm.functions.Function0
                public /* bridge */ /* synthetic */ Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2() {
                    SetupActivity.this.startPairing();
                }
            });
        }
    }

    private final void show(int title, int body, Boolean done) {
        ActivitySetupBinding activitySetupBinding = this.b;
        ActivitySetupBinding activitySetupBinding2 = null;
        if (activitySetupBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding = null;
        }
        activitySetupBinding.tvTitle.setText(title);
        ActivitySetupBinding activitySetupBinding3 = this.b;
        if (activitySetupBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding3 = null;
        }
        activitySetupBinding3.tvBody.setText(body);
        ActivitySetupBinding activitySetupBinding4 = this.b;
        if (activitySetupBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding4 = null;
        }
        TextView tvStatus = activitySetupBinding4.tvStatus;
        Intrinsics.checkNotNullExpressionValue(tvStatus, "tvStatus");
        tvStatus.setVisibility(done != null ? 0 : 8);
        if (done != null) {
            ActivitySetupBinding activitySetupBinding5 = this.b;
            if (activitySetupBinding5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("b");
                activitySetupBinding5 = null;
            }
            activitySetupBinding5.tvStatus.setText(done.booleanValue() ? R.string.setup_status_done : R.string.setup_status_open);
            ActivitySetupBinding activitySetupBinding6 = this.b;
            if (activitySetupBinding6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("b");
                activitySetupBinding6 = null;
            }
            activitySetupBinding6.tvStatus.setTextColor(done.booleanValue() ? COLOR_OK : COLOR_OPEN);
        }
        boolean areEqual = Intrinsics.areEqual((Object) done, (Object) false);
        boolean z = !areEqual;
        ActivitySetupBinding activitySetupBinding7 = this.b;
        if (activitySetupBinding7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding7 = null;
        }
        Button btnNext = activitySetupBinding7.btnNext;
        Intrinsics.checkNotNullExpressionValue(btnNext, "btnNext");
        btnNext.setVisibility(z ? 0 : 8);
        ActivitySetupBinding activitySetupBinding8 = this.b;
        if (activitySetupBinding8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding8 = null;
        }
        activitySetupBinding8.btnNext.setText(R.string.setup_next);
        ActivitySetupBinding activitySetupBinding9 = this.b;
        if (activitySetupBinding9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
        } else {
            activitySetupBinding2 = activitySetupBinding9;
        }
        Button btnSkip = activitySetupBinding2.btnSkip;
        Intrinsics.checkNotNullExpressionValue(btnSkip, "btnSkip");
        btnSkip.setVisibility(areEqual ? 0 : 8);
    }

    private final void action(int text, final Function0<Unit> onClick) {
        ActivitySetupBinding activitySetupBinding = this.b;
        ActivitySetupBinding activitySetupBinding2 = null;
        if (activitySetupBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
            activitySetupBinding = null;
        }
        activitySetupBinding.btnAction.setText(text);
        ActivitySetupBinding activitySetupBinding3 = this.b;
        if (activitySetupBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("b");
        } else {
            activitySetupBinding2 = activitySetupBinding3;
        }
        activitySetupBinding2.btnAction.setOnClickListener(new View.OnClickListener() { // from class: com.mapsbledisplay.SetupActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SetupActivity.action$lambda$8(Function0.this, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void action$lambda$8(Function0 onClick, View view) {
        Intrinsics.checkNotNullParameter(onClick, "$onClick");
        onClick.invoke();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startPairing() {
        if (Build.VERSION.SDK_INT < 31) {
            return;
        }
        SetupActivity setupActivity = this;
        if (!Permissions.INSTANCE.hasBle(setupActivity)) {
            this.permissionLauncher.launch(Permissions.INSTANCE.requestable());
        } else if (!BleManager.INSTANCE.isBluetoothOn()) {
            this.simpleLauncher.launch(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
        } else {
            CompanionPairing.INSTANCE.start(setupActivity, new Function1<IntentSender, Unit>() { // from class: com.mapsbledisplay.SetupActivity$startPairing$1
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(IntentSender intentSender) {
                    invoke2(intentSender);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(IntentSender it) {
                    ActivityResultLauncher activityResultLauncher;
                    Intrinsics.checkNotNullParameter(it, "it");
                    activityResultLauncher = SetupActivity.this.pairLauncher;
                    activityResultLauncher.launch(new IntentSenderRequest.Builder(it).build());
                }
            }, new Function1<String, Unit>() { // from class: com.mapsbledisplay.SetupActivity$startPairing$2
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
                    CompanionPairing.INSTANCE.finish(SetupActivity.this);
                    Toast.makeText(SetupActivity.this, R.string.pair_failed, 1).show();
                    SetupActivity.this.render();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void requestBatteryExemption() {
        try {
            this.simpleLauncher.launch(new Intent("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", Uri.parse("package:" + getPackageName())));
        } catch (Exception unused) {
            this.simpleLauncher.launch(new Intent("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS"));
        }
    }
}
