package kotlinx.coroutines.flow.internal;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CompletableJob;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.JobKt__JobKt;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.channels.SendChannel;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.internal.ThreadContextKt;

/* compiled from: Combine.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004*\u00020\u0005H\u008a@"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1", f = "Combine.kt", i = {0, 0}, l = {123}, m = "invokeSuspend", n = {"second", "collectJob"}, s = {"L$0", "L$1"})
/* loaded from: classes.dex */
final class CombineKt$zipImpl$1$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow<T1> $flow;
    final /* synthetic */ Flow<T2> $flow2;
    final /* synthetic */ FlowCollector<R> $this_unsafeFlow;
    final /* synthetic */ Function3<T1, T2, Continuation<? super R>, Object> $transform;
    private /* synthetic */ Object L$0;
    Object L$1;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    CombineKt$zipImpl$1$1(Flow<? extends T2> flow, Flow<? extends T1> flow2, FlowCollector<? super R> flowCollector, Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3, Continuation<? super CombineKt$zipImpl$1$1> continuation) {
        super(2, continuation);
        this.$flow2 = flow;
        this.$flow = flow2;
        this.$this_unsafeFlow = flowCollector;
        this.$transform = function3;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        CombineKt$zipImpl$1$1 combineKt$zipImpl$1$1 = new CombineKt$zipImpl$1$1(this.$flow2, this.$flow, this.$this_unsafeFlow, this.$transform, continuation);
        combineKt$zipImpl$1$1.L$0 = obj;
        return combineKt$zipImpl$1$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((CombineKt$zipImpl$1$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        ReceiveChannel produce$default;
        final CompletableJob Job$default;
        CompletableJob completableJob;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        try {
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                CoroutineScope coroutineScope = (CoroutineScope) this.L$0;
                produce$default = ProduceKt.produce$default(coroutineScope, null, 0, new CombineKt$zipImpl$1$1$second$1(this.$flow2, null), 3, null);
                Job$default = JobKt__JobKt.Job$default((Job) null, 1, (Object) null);
                Intrinsics.checkNotNull(produce$default, "null cannot be cast to non-null type kotlinx.coroutines.channels.SendChannel<*>");
                ((SendChannel) produce$default).invokeOnClose(new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1.1
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                        invoke2(th);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(Throwable th) {
                        if (CompletableJob.this.isActive()) {
                            CompletableJob.this.cancel((CancellationException) new AbortFlowException(CompletableJob.this));
                        }
                    }
                });
                try {
                    CoroutineContext coroutineContext = coroutineScope.getCoroutineContext();
                    Object threadContextElements = ThreadContextKt.threadContextElements(coroutineContext);
                    this.L$0 = produce$default;
                    this.L$1 = Job$default;
                    this.label = 1;
                    if (ChannelFlowKt.withContextUndispatched$default(coroutineScope.getCoroutineContext().plus(Job$default), Unit.INSTANCE, null, new AnonymousClass2(this.$flow, coroutineContext, threadContextElements, produce$default, this.$this_unsafeFlow, this.$transform, Job$default, null), this, 4, null) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } catch (AbortFlowException e) {
                    e = e;
                    completableJob = Job$default;
                    FlowExceptions_commonKt.checkOwnership(e, completableJob);
                    return Unit.INSTANCE;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                completableJob = (CompletableJob) this.L$1;
                produce$default = (ReceiveChannel) this.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                } catch (AbortFlowException e2) {
                    e = e2;
                    FlowExceptions_commonKt.checkOwnership(e, completableJob);
                    return Unit.INSTANCE;
                }
            }
            return Unit.INSTANCE;
        } finally {
            ReceiveChannel.DefaultImpls.cancel$default(produce$default, (CancellationException) null, 1, (Object) null);
        }
    }

    /* compiled from: Combine.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u00042\u0006\u0010\u0005\u001a\u00020\u0001H\u008a@"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "it"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2", f = "Combine.kt", i = {}, l = {124}, m = "invokeSuspend", n = {}, s = {})
    /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2, reason: invalid class name */
    static final class AnonymousClass2 extends SuspendLambda implements Function2<Unit, Continuation<? super Unit>, Object> {
        final /* synthetic */ Object $cnt;
        final /* synthetic */ CompletableJob $collectJob;
        final /* synthetic */ Flow<T1> $flow;
        final /* synthetic */ CoroutineContext $scopeContext;
        final /* synthetic */ ReceiveChannel<Object> $second;
        final /* synthetic */ FlowCollector<R> $this_unsafeFlow;
        final /* synthetic */ Function3<T1, T2, Continuation<? super R>, Object> $transform;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        AnonymousClass2(Flow<? extends T1> flow, CoroutineContext coroutineContext, Object obj, ReceiveChannel<? extends Object> receiveChannel, FlowCollector<? super R> flowCollector, Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3, CompletableJob completableJob, Continuation<? super AnonymousClass2> continuation) {
            super(2, continuation);
            this.$flow = flow;
            this.$scopeContext = coroutineContext;
            this.$cnt = obj;
            this.$second = receiveChannel;
            this.$this_unsafeFlow = flowCollector;
            this.$transform = function3;
            this.$collectJob = completableJob;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new AnonymousClass2(this.$flow, this.$scopeContext, this.$cnt, this.$second, this.$this_unsafeFlow, this.$transform, this.$collectJob, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(Unit unit, Continuation<? super Unit> continuation) {
            return ((AnonymousClass2) create(unit, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* compiled from: Combine.kt */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u00042\u0006\u0010\u0005\u001a\u0002H\u0002H\u008a@¢\u0006\u0004\b\u0006\u0010\u0007"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1, reason: invalid class name */
        static final class AnonymousClass1<T> implements FlowCollector {
            final /* synthetic */ Object $cnt;
            final /* synthetic */ CompletableJob $collectJob;
            final /* synthetic */ CoroutineContext $scopeContext;
            final /* synthetic */ ReceiveChannel<Object> $second;
            final /* synthetic */ FlowCollector<R> $this_unsafeFlow;
            final /* synthetic */ Function3<T1, T2, Continuation<? super R>, Object> $transform;

            /* JADX WARN: Multi-variable type inference failed */
            AnonymousClass1(CoroutineContext coroutineContext, Object obj, ReceiveChannel<? extends Object> receiveChannel, FlowCollector<? super R> flowCollector, Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3, CompletableJob completableJob) {
                this.$scopeContext = coroutineContext;
                this.$cnt = obj;
                this.$second = receiveChannel;
                this.$this_unsafeFlow = flowCollector;
                this.$transform = function3;
                this.$collectJob = completableJob;
            }

            /* compiled from: Combine.kt */
            @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u00042\u0006\u0010\u0005\u001a\u00020\u0001H\u008a@"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "it"}, k = 3, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
            @DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$1", f = "Combine.kt", i = {}, l = {126, 129, 129}, m = "invokeSuspend", n = {}, s = {})
            /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$1, reason: invalid class name and collision with other inner class name */
            static final class C00121 extends SuspendLambda implements Function2<Unit, Continuation<? super Unit>, Object> {
                final /* synthetic */ CompletableJob $collectJob;
                final /* synthetic */ ReceiveChannel<Object> $second;
                final /* synthetic */ FlowCollector<R> $this_unsafeFlow;
                final /* synthetic */ Function3<T1, T2, Continuation<? super R>, Object> $transform;
                final /* synthetic */ T1 $value;
                Object L$0;
                int label;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                /* JADX WARN: Multi-variable type inference failed */
                C00121(ReceiveChannel<? extends Object> receiveChannel, FlowCollector<? super R> flowCollector, Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3, T1 t1, CompletableJob completableJob, Continuation<? super C00121> continuation) {
                    super(2, continuation);
                    this.$second = receiveChannel;
                    this.$this_unsafeFlow = flowCollector;
                    this.$transform = function3;
                    this.$value = t1;
                    this.$collectJob = completableJob;
                }

                @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
                public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                    return new C00121(this.$second, this.$this_unsafeFlow, this.$transform, this.$value, this.$collectJob, continuation);
                }

                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Unit unit, Continuation<? super Unit> continuation) {
                    return ((C00121) create(unit, continuation)).invokeSuspend(Unit.INSTANCE);
                }

                /* JADX WARN: Multi-variable type inference failed */
                /* JADX WARN: Removed duplicated region for block: B:15:0x0078 A[RETURN] */
                /* JADX WARN: Type inference failed for: r1v5, types: [kotlinx.coroutines.flow.FlowCollector] */
                /* JADX WARN: Type inference failed for: r1v8 */
                /* JADX WARN: Type inference failed for: r1v9 */
                @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public final java.lang.Object invokeSuspend(java.lang.Object r9) {
                    /*
                        r8 = this;
                        java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                        int r1 = r8.label
                        r2 = 0
                        r3 = 3
                        r4 = 2
                        r5 = 1
                        if (r1 == 0) goto L30
                        if (r1 == r5) goto L26
                        if (r1 == r4) goto L1e
                        if (r1 != r3) goto L16
                        kotlin.ResultKt.throwOnFailure(r9)
                        goto L79
                    L16:
                        java.lang.IllegalStateException r9 = new java.lang.IllegalStateException
                        java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                        r9.<init>(r0)
                        throw r9
                    L1e:
                        java.lang.Object r1 = r8.L$0
                        kotlinx.coroutines.flow.FlowCollector r1 = (kotlinx.coroutines.flow.FlowCollector) r1
                        kotlin.ResultKt.throwOnFailure(r9)
                        goto L6b
                    L26:
                        kotlin.ResultKt.throwOnFailure(r9)
                        kotlinx.coroutines.channels.ChannelResult r9 = (kotlinx.coroutines.channels.ChannelResult) r9
                        java.lang.Object r9 = r9.getHolder()
                        goto L41
                    L30:
                        kotlin.ResultKt.throwOnFailure(r9)
                        kotlinx.coroutines.channels.ReceiveChannel<java.lang.Object> r9 = r8.$second
                        r1 = r8
                        kotlin.coroutines.Continuation r1 = (kotlin.coroutines.Continuation) r1
                        r8.label = r5
                        java.lang.Object r9 = r9.mo1677receiveCatchingJP2dKIU(r1)
                        if (r9 != r0) goto L41
                        return r0
                    L41:
                        kotlinx.coroutines.CompletableJob r1 = r8.$collectJob
                        boolean r5 = r9 instanceof kotlinx.coroutines.channels.ChannelResult.Failed
                        if (r5 == 0) goto L55
                        java.lang.Throwable r9 = kotlinx.coroutines.channels.ChannelResult.m1688exceptionOrNullimpl(r9)
                        if (r9 != 0) goto L54
                        kotlinx.coroutines.flow.internal.AbortFlowException r9 = new kotlinx.coroutines.flow.internal.AbortFlowException
                        r9.<init>(r1)
                        java.lang.Throwable r9 = (java.lang.Throwable) r9
                    L54:
                        throw r9
                    L55:
                        kotlinx.coroutines.flow.FlowCollector<R> r1 = r8.$this_unsafeFlow
                        kotlin.jvm.functions.Function3<T1, T2, kotlin.coroutines.Continuation<? super R>, java.lang.Object> r5 = r8.$transform
                        T1 r6 = r8.$value
                        kotlinx.coroutines.internal.Symbol r7 = kotlinx.coroutines.flow.internal.NullSurrogateKt.NULL
                        if (r9 != r7) goto L60
                        r9 = r2
                    L60:
                        r8.L$0 = r1
                        r8.label = r4
                        java.lang.Object r9 = r5.invoke(r6, r9, r8)
                        if (r9 != r0) goto L6b
                        return r0
                    L6b:
                        r4 = r8
                        kotlin.coroutines.Continuation r4 = (kotlin.coroutines.Continuation) r4
                        r8.L$0 = r2
                        r8.label = r3
                        java.lang.Object r9 = r1.emit(r9, r4)
                        if (r9 != r0) goto L79
                        return r0
                    L79:
                        kotlin.Unit r9 = kotlin.Unit.INSTANCE
                        return r9
                    */
                    throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1.AnonymousClass2.AnonymousClass1.C00121.invokeSuspend(java.lang.Object):java.lang.Object");
                }
            }

            /* JADX WARN: Removed duplicated region for block: B:15:0x0032  */
            /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
            @Override // kotlinx.coroutines.flow.FlowCollector
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public final java.lang.Object emit(T1 r14, kotlin.coroutines.Continuation<? super kotlin.Unit> r15) {
                /*
                    r13 = this;
                    boolean r0 = r15 instanceof kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$emit$1
                    if (r0 == 0) goto L14
                    r0 = r15
                    kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$emit$1 r0 = (kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$emit$1) r0
                    int r1 = r0.label
                    r2 = -2147483648(0xffffffff80000000, float:-0.0)
                    r1 = r1 & r2
                    if (r1 == 0) goto L14
                    int r15 = r0.label
                    int r15 = r15 - r2
                    r0.label = r15
                    goto L19
                L14:
                    kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$emit$1 r0 = new kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$emit$1
                    r0.<init>(r13, r15)
                L19:
                    java.lang.Object r15 = r0.result
                    java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                    int r2 = r0.label
                    r3 = 1
                    if (r2 == 0) goto L32
                    if (r2 != r3) goto L2a
                    kotlin.ResultKt.throwOnFailure(r15)
                    goto L56
                L2a:
                    java.lang.IllegalStateException r14 = new java.lang.IllegalStateException
                    java.lang.String r15 = "call to 'resume' before 'invoke' with coroutine"
                    r14.<init>(r15)
                    throw r14
                L32:
                    kotlin.ResultKt.throwOnFailure(r15)
                    kotlin.coroutines.CoroutineContext r15 = r13.$scopeContext
                    kotlin.Unit r2 = kotlin.Unit.INSTANCE
                    java.lang.Object r4 = r13.$cnt
                    kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$1 r12 = new kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$1$1
                    kotlinx.coroutines.channels.ReceiveChannel<java.lang.Object> r6 = r13.$second
                    kotlinx.coroutines.flow.FlowCollector<R> r7 = r13.$this_unsafeFlow
                    kotlin.jvm.functions.Function3<T1, T2, kotlin.coroutines.Continuation<? super R>, java.lang.Object> r8 = r13.$transform
                    kotlinx.coroutines.CompletableJob r10 = r13.$collectJob
                    r11 = 0
                    r5 = r12
                    r9 = r14
                    r5.<init>(r6, r7, r8, r9, r10, r11)
                    kotlin.jvm.functions.Function2 r12 = (kotlin.jvm.functions.Function2) r12
                    r0.label = r3
                    java.lang.Object r14 = kotlinx.coroutines.flow.internal.ChannelFlowKt.withContextUndispatched(r15, r2, r4, r12, r0)
                    if (r14 != r1) goto L56
                    return r1
                L56:
                    kotlin.Unit r14 = kotlin.Unit.INSTANCE
                    return r14
                */
                throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1.AnonymousClass2.AnonymousClass1.emit(java.lang.Object, kotlin.coroutines.Continuation):java.lang.Object");
            }
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                if (this.$flow.collect(new AnonymousClass1(this.$scopeContext, this.$cnt, this.$second, this.$this_unsafeFlow, this.$transform, this.$collectJob), this) == coroutine_suspended) {
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
}
