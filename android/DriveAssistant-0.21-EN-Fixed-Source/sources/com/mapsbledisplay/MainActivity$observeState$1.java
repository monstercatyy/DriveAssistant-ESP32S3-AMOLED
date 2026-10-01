package com.mapsbledisplay;

import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.RepeatOnLifecycleKt;
import com.mapsbledisplay.BleManager;
import com.mapsbledisplay.databinding.ActivityMainBinding;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1", f = "MainActivity.kt", i = {}, l = {279}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: classes.dex */
final class MainActivity$observeState$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ MainActivity this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    MainActivity$observeState$1(MainActivity mainActivity, Continuation<? super MainActivity$observeState$1> continuation) {
        super(2, continuation);
        this.this$0 = mainActivity;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new MainActivity$observeState$1(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((MainActivity$observeState$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1$1", f = "MainActivity.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
    /* renamed from: com.mapsbledisplay.MainActivity$observeState$1$1, reason: invalid class name */
    static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        private /* synthetic */ Object L$0;
        int label;
        final /* synthetic */ MainActivity this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(MainActivity mainActivity, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.this$0 = mainActivity;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.this$0, continuation);
            anonymousClass1.L$0 = obj;
            return anonymousClass1;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* compiled from: MainActivity.kt */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        @DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1$1$1", f = "MainActivity.kt", i = {}, l = {281}, m = "invokeSuspend", n = {}, s = {})
        /* renamed from: com.mapsbledisplay.MainActivity$observeState$1$1$1, reason: invalid class name and collision with other inner class name */
        static final class C00051 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            int label;
            final /* synthetic */ MainActivity this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            C00051(MainActivity mainActivity, Continuation<? super C00051> continuation) {
                super(2, continuation);
                this.this$0 = mainActivity;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return new C00051(this.this$0, continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((C00051) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Object invokeSuspend(Object obj) {
                Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                int i = this.label;
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    StateFlow<BleManager.State> state = BleManager.INSTANCE.getState();
                    final MainActivity mainActivity = this.this$0;
                    this.label = 1;
                    if (state.collect(new FlowCollector() { // from class: com.mapsbledisplay.MainActivity.observeState.1.1.1.1
                        public final Object emit(BleManager.State state2, Continuation<? super Unit> continuation) {
                            MainActivity.this.refreshBleStatus(state2);
                            return Unit.INSTANCE;
                        }

                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public /* bridge */ /* synthetic */ Object emit(Object obj2, Continuation continuation) {
                            return emit((BleManager.State) obj2, (Continuation<? super Unit>) continuation);
                        }
                    }, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                throw new KotlinNothingValueException();
            }
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label == 0) {
                ResultKt.throwOnFailure(obj);
                CoroutineScope coroutineScope = (CoroutineScope) this.L$0;
                BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new C00051(this.this$0, null), 3, null);
                BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new AnonymousClass2(this.this$0, null), 3, null);
                BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new AnonymousClass3(this.this$0, null), 3, null);
                BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new AnonymousClass4(this.this$0, null), 3, null);
                BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new AnonymousClass5(this.this$0, null), 3, null);
                return Unit.INSTANCE;
            }
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }

        /* compiled from: MainActivity.kt */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        @DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1$1$2", f = "MainActivity.kt", i = {}, l = {284}, m = "invokeSuspend", n = {}, s = {})
        /* renamed from: com.mapsbledisplay.MainActivity$observeState$1$1$2, reason: invalid class name */
        static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            int label;
            final /* synthetic */ MainActivity this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass2(MainActivity mainActivity, Continuation<? super AnonymousClass2> continuation) {
                super(2, continuation);
                this.this$0 = mainActivity;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return new AnonymousClass2(this.this$0, continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((AnonymousClass2) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Object invokeSuspend(Object obj) {
                Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                int i = this.label;
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    StateFlow<Boolean> active = BackgroundScan.INSTANCE.getActive();
                    final MainActivity mainActivity = this.this$0;
                    this.label = 1;
                    if (active.collect(new FlowCollector() { // from class: com.mapsbledisplay.MainActivity.observeState.1.1.2.1
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public /* bridge */ /* synthetic */ Object emit(Object obj2, Continuation continuation) {
                            return emit(((Boolean) obj2).booleanValue(), (Continuation<? super Unit>) continuation);
                        }

                        public final Object emit(boolean z, Continuation<? super Unit> continuation) {
                            MainActivity.this.refreshBleStatus(BleManager.INSTANCE.getState().getValue());
                            return Unit.INSTANCE;
                        }
                    }, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                throw new KotlinNothingValueException();
            }
        }

        /* compiled from: MainActivity.kt */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        @DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1$1$3", f = "MainActivity.kt", i = {}, l = {287}, m = "invokeSuspend", n = {}, s = {})
        /* renamed from: com.mapsbledisplay.MainActivity$observeState$1$1$3, reason: invalid class name */
        static final class AnonymousClass3 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            int label;
            final /* synthetic */ MainActivity this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass3(MainActivity mainActivity, Continuation<? super AnonymousClass3> continuation) {
                super(2, continuation);
                this.this$0 = mainActivity;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return new AnonymousClass3(this.this$0, continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((AnonymousClass3) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Object invokeSuspend(Object obj) {
                Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                int i = this.label;
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    StateFlow<BleManager.State> state = BleManager.INSTANCE.getState();
                    final MainActivity mainActivity = this.this$0;
                    this.label = 1;
                    if (state.collect(new FlowCollector() { // from class: com.mapsbledisplay.MainActivity.observeState.1.1.3.1
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public /* bridge */ /* synthetic */ Object emit(Object obj2, Continuation continuation) {
                            return emit((BleManager.State) obj2, (Continuation<? super Unit>) continuation);
                        }

                        public final Object emit(BleManager.State state2, Continuation<? super Unit> continuation) {
                            ActivityMainBinding activityMainBinding;
                            ActivityMainBinding activityMainBinding2;
                            ActivityMainBinding activityMainBinding3;
                            activityMainBinding = MainActivity.this.binding;
                            ActivityMainBinding activityMainBinding4 = null;
                            if (activityMainBinding == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                activityMainBinding = null;
                            }
                            activityMainBinding.btnConnect.setText(MainActivity.this.getString((state2 == BleManager.State.CONNECTED || state2 == BleManager.State.CONNECTING) ? R.string.btn_disconnect : R.string.btn_connect));
                            activityMainBinding2 = MainActivity.this.binding;
                            if (activityMainBinding2 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                activityMainBinding2 = null;
                            }
                            activityMainBinding2.btnTest.setEnabled(state2 == BleManager.State.CONNECTED);
                            MainActivity mainActivity2 = MainActivity.this;
                            activityMainBinding3 = mainActivity2.binding;
                            if (activityMainBinding3 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                            } else {
                                activityMainBinding4 = activityMainBinding3;
                            }
                            TextView dotStep2 = activityMainBinding4.dotStep2;
                            Intrinsics.checkNotNullExpressionValue(dotStep2, "dotStep2");
                            mainActivity2.setDot(dotStep2, state2 == BleManager.State.CONNECTED);
                            return Unit.INSTANCE;
                        }
                    }, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                throw new KotlinNothingValueException();
            }
        }

        /* compiled from: MainActivity.kt */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        @DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1$1$4", f = "MainActivity.kt", i = {}, l = {297}, m = "invokeSuspend", n = {}, s = {})
        /* renamed from: com.mapsbledisplay.MainActivity$observeState$1$1$4, reason: invalid class name */
        static final class AnonymousClass4 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            int label;
            final /* synthetic */ MainActivity this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass4(MainActivity mainActivity, Continuation<? super AnonymousClass4> continuation) {
                super(2, continuation);
                this.this$0 = mainActivity;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return new AnonymousClass4(this.this$0, continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((AnonymousClass4) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Object invokeSuspend(Object obj) {
                Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                int i = this.label;
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    StateFlow<String> lastSent = BleManager.INSTANCE.getLastSent();
                    final MainActivity mainActivity = this.this$0;
                    this.label = 1;
                    if (lastSent.collect(new FlowCollector() { // from class: com.mapsbledisplay.MainActivity.observeState.1.1.4.1
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public /* bridge */ /* synthetic */ Object emit(Object obj2, Continuation continuation) {
                            return emit((String) obj2, (Continuation<? super Unit>) continuation);
                        }

                        public final Object emit(String str, Continuation<? super Unit> continuation) {
                            ActivityMainBinding activityMainBinding;
                            String string;
                            ActivityMainBinding activityMainBinding2;
                            String string2;
                            activityMainBinding = MainActivity.this.binding;
                            ActivityMainBinding activityMainBinding3 = null;
                            if (activityMainBinding == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                activityMainBinding = null;
                            }
                            TextView textView = activityMainBinding.tvLastSent;
                            if (str != null && (string2 = MainActivity.this.getString(R.string.last_sent, new Object[]{str})) != null) {
                                string = string2;
                            } else {
                                string = MainActivity.this.getString(R.string.last_sent_none);
                            }
                            textView.setText(string);
                            MainActivity mainActivity2 = MainActivity.this;
                            activityMainBinding2 = mainActivity2.binding;
                            if (activityMainBinding2 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                            } else {
                                activityMainBinding3 = activityMainBinding2;
                            }
                            TextView dotStep3 = activityMainBinding3.dotStep3;
                            Intrinsics.checkNotNullExpressionValue(dotStep3, "dotStep3");
                            mainActivity2.setDot(dotStep3, str != null);
                            return Unit.INSTANCE;
                        }
                    }, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                throw new KotlinNothingValueException();
            }
        }

        /* compiled from: MainActivity.kt */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        @DebugMetadata(c = "com.mapsbledisplay.MainActivity$observeState$1$1$5", f = "MainActivity.kt", i = {}, l = {306}, m = "invokeSuspend", n = {}, s = {})
        /* renamed from: com.mapsbledisplay.MainActivity$observeState$1$1$5, reason: invalid class name */
        static final class AnonymousClass5 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            int label;
            final /* synthetic */ MainActivity this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass5(MainActivity mainActivity, Continuation<? super AnonymousClass5> continuation) {
                super(2, continuation);
                this.this$0 = mainActivity;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return new AnonymousClass5(this.this$0, continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((AnonymousClass5) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Object invokeSuspend(Object obj) {
                Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                int i = this.label;
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    MutableStateFlow<Boolean> connected = MapsNotificationListenerService.INSTANCE.getConnected();
                    final MainActivity mainActivity = this.this$0;
                    this.label = 1;
                    if (connected.collect(new FlowCollector() { // from class: com.mapsbledisplay.MainActivity.observeState.1.1.5.1
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public /* bridge */ /* synthetic */ Object emit(Object obj2, Continuation continuation) {
                            return emit(((Boolean) obj2).booleanValue(), (Continuation<? super Unit>) continuation);
                        }

                        public final Object emit(boolean z, Continuation<? super Unit> continuation) {
                            MainActivity.this.refreshNotifAccess();
                            return Unit.INSTANCE;
                        }
                    }, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                throw new KotlinNothingValueException();
            }
        }
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            this.label = 1;
            if (RepeatOnLifecycleKt.repeatOnLifecycle(this.this$0, Lifecycle.State.STARTED, new AnonymousClass1(this.this$0, null), this) == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
        }
        return Unit.INSTANCE;
    }
}
