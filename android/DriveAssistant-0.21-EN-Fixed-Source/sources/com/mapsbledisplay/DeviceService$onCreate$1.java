package com.mapsbledisplay;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.mapsbledisplay.BleManager;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.StateFlow;

/* compiled from: DeviceService.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@DebugMetadata(c = "com.mapsbledisplay.DeviceService$onCreate$1", f = "DeviceService.kt", i = {}, l = {126}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: classes.dex */
final class DeviceService$onCreate$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ DeviceService this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    DeviceService$onCreate$1(DeviceService deviceService, Continuation<? super DeviceService$onCreate$1> continuation) {
        super(2, continuation);
        this.this$0 = deviceService;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new DeviceService$onCreate$1(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((DeviceService$onCreate$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            StateFlow<BleManager.State> state = BleManager.INSTANCE.getState();
            final DeviceService deviceService = this.this$0;
            this.label = 1;
            if (state.collect(new FlowCollector() { // from class: com.mapsbledisplay.DeviceService$onCreate$1.1
                public final Object emit(BleManager.State state2, Continuation<? super Unit> continuation) {
                    DeviceService.this.onBleState(state2);
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
