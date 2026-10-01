package kotlin;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.jvm.JvmInline;
import kotlin.ranges.UIntRange;
import kotlin.ranges.URangesKt;

/* compiled from: UInt.kt */
@Metadata(d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b!\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\n\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000e\b\u0087@\u0018\u0000 {2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001{B\u0011\b\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\f¢\u0006\u0004\b\n\u0010\u000bJ\u0018\u0010\f\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\rH\u0087\n¢\u0006\u0004\b\u000e\u0010\u000fJ\u0018\u0010\f\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0000H\u0097\n¢\u0006\u0004\b\u0010\u0010\u000bJ\u0018\u0010\f\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0011H\u0087\n¢\u0006\u0004\b\u0012\u0010\u0013J\u0018\u0010\f\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0014H\u0087\n¢\u0006\u0004\b\u0015\u0010\u0016J\u0013\u0010\u0017\u001a\u00020\u0000H\u0087\nø\u0001\u0000¢\u0006\u0004\b\u0018\u0010\u0005J\u0018\u0010\u0019\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\rH\u0087\n¢\u0006\u0004\b\u001a\u0010\u000fJ\u0018\u0010\u0019\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b\u001b\u0010\u000bJ\u0018\u0010\u0019\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\n¢\u0006\u0004\b\u001c\u0010\u001dJ\u0018\u0010\u0019\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014H\u0087\n¢\u0006\u0004\b\u001e\u0010\u0016J\u001a\u0010\u001f\u001a\u00020 2\b\u0010\t\u001a\u0004\u0018\u00010!HÖ\u0003¢\u0006\u0004\b\"\u0010#J\u0018\u0010$\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\rH\u0087\b¢\u0006\u0004\b%\u0010\u000fJ\u0018\u0010$\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\b¢\u0006\u0004\b&\u0010\u000bJ\u0018\u0010$\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\b¢\u0006\u0004\b'\u0010\u001dJ\u0018\u0010$\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014H\u0087\b¢\u0006\u0004\b(\u0010\u0016J\u0010\u0010)\u001a\u00020\u0003HÖ\u0001¢\u0006\u0004\b*\u0010\u0005J\u0013\u0010+\u001a\u00020\u0000H\u0087\nø\u0001\u0000¢\u0006\u0004\b,\u0010\u0005J\u0013\u0010-\u001a\u00020\u0000H\u0087\bø\u0001\u0000¢\u0006\u0004\b.\u0010\u0005J\u0018\u0010/\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\rH\u0087\n¢\u0006\u0004\b0\u0010\u000fJ\u0018\u0010/\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b1\u0010\u000bJ\u0018\u0010/\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\n¢\u0006\u0004\b2\u0010\u001dJ\u0018\u0010/\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014H\u0087\n¢\u0006\u0004\b3\u0010\u0016J\u0018\u00104\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\rH\u0087\b¢\u0006\u0004\b5\u00106J\u0018\u00104\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\b¢\u0006\u0004\b7\u0010\u000bJ\u0018\u00104\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\b¢\u0006\u0004\b8\u0010\u001dJ\u0018\u00104\u001a\u00020\u00142\u0006\u0010\t\u001a\u00020\u0014H\u0087\b¢\u0006\u0004\b9\u0010:J\u0018\u0010;\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\f¢\u0006\u0004\b<\u0010\u000bJ\u0018\u0010=\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\rH\u0087\n¢\u0006\u0004\b>\u0010\u000fJ\u0018\u0010=\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b?\u0010\u000bJ\u0018\u0010=\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\n¢\u0006\u0004\b@\u0010\u001dJ\u0018\u0010=\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014H\u0087\n¢\u0006\u0004\bA\u0010\u0016J\u0018\u0010B\u001a\u00020C2\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\bD\u0010EJ\u0018\u0010F\u001a\u00020C2\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\bG\u0010EJ\u0018\u0010H\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\rH\u0087\n¢\u0006\u0004\bI\u0010\u000fJ\u0018\u0010H\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\bJ\u0010\u000bJ\u0018\u0010H\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\n¢\u0006\u0004\bK\u0010\u001dJ\u0018\u0010H\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014H\u0087\n¢\u0006\u0004\bL\u0010\u0016J\u001b\u0010M\u001a\u00020\u00002\u0006\u0010N\u001a\u00020\u0003H\u0087\fø\u0001\u0000¢\u0006\u0004\bO\u0010\u000bJ\u001b\u0010P\u001a\u00020\u00002\u0006\u0010N\u001a\u00020\u0003H\u0087\fø\u0001\u0000¢\u0006\u0004\bQ\u0010\u000bJ\u0018\u0010R\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\rH\u0087\n¢\u0006\u0004\bS\u0010\u000fJ\u0018\u0010R\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\bT\u0010\u000bJ\u0018\u0010R\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0011H\u0087\n¢\u0006\u0004\bU\u0010\u001dJ\u0018\u0010R\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0014H\u0087\n¢\u0006\u0004\bV\u0010\u0016J\u0010\u0010W\u001a\u00020XH\u0087\b¢\u0006\u0004\bY\u0010ZJ\u0010\u0010[\u001a\u00020\\H\u0087\b¢\u0006\u0004\b]\u0010^J\u0010\u0010_\u001a\u00020`H\u0087\b¢\u0006\u0004\ba\u0010bJ\u0010\u0010c\u001a\u00020\u0003H\u0087\b¢\u0006\u0004\bd\u0010\u0005J\u0010\u0010e\u001a\u00020fH\u0087\b¢\u0006\u0004\bg\u0010hJ\u0010\u0010i\u001a\u00020jH\u0087\b¢\u0006\u0004\bk\u0010lJ\u000f\u0010m\u001a\u00020nH\u0016¢\u0006\u0004\bo\u0010pJ\u0013\u0010q\u001a\u00020\rH\u0087\bø\u0001\u0000¢\u0006\u0004\br\u0010ZJ\u0013\u0010s\u001a\u00020\u0000H\u0087\bø\u0001\u0000¢\u0006\u0004\bt\u0010\u0005J\u0013\u0010u\u001a\u00020\u0011H\u0087\bø\u0001\u0000¢\u0006\u0004\bv\u0010hJ\u0013\u0010w\u001a\u00020\u0014H\u0087\bø\u0001\u0000¢\u0006\u0004\bx\u0010lJ\u0018\u0010y\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0000H\u0087\f¢\u0006\u0004\bz\u0010\u000bR\u0016\u0010\u0002\u001a\u00020\u00038\u0000X\u0081\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0006\u0010\u0007\u0088\u0001\u0002\u0092\u0001\u00020\u0003\u0082\u0002\u0004\n\u0002\b!¨\u0006|"}, d2 = {"Lkotlin/UInt;", "", "data", "", "constructor-impl", "(I)I", "getData$annotations", "()V", "and", "other", "and-WZ4Q5Ns", "(II)I", "compareTo", "Lkotlin/UByte;", "compareTo-7apg3OU", "(IB)I", "compareTo-WZ4Q5Ns", "Lkotlin/ULong;", "compareTo-VKZWuLQ", "(IJ)I", "Lkotlin/UShort;", "compareTo-xj2QHRw", "(IS)I", "dec", "dec-pVg5ArA", "div", "div-7apg3OU", "div-WZ4Q5Ns", "div-VKZWuLQ", "(IJ)J", "div-xj2QHRw", "equals", "", "", "equals-impl", "(ILjava/lang/Object;)Z", "floorDiv", "floorDiv-7apg3OU", "floorDiv-WZ4Q5Ns", "floorDiv-VKZWuLQ", "floorDiv-xj2QHRw", "hashCode", "hashCode-impl", "inc", "inc-pVg5ArA", "inv", "inv-pVg5ArA", "minus", "minus-7apg3OU", "minus-WZ4Q5Ns", "minus-VKZWuLQ", "minus-xj2QHRw", "mod", "mod-7apg3OU", "(IB)B", "mod-WZ4Q5Ns", "mod-VKZWuLQ", "mod-xj2QHRw", "(IS)S", "or", "or-WZ4Q5Ns", "plus", "plus-7apg3OU", "plus-WZ4Q5Ns", "plus-VKZWuLQ", "plus-xj2QHRw", "rangeTo", "Lkotlin/ranges/UIntRange;", "rangeTo-WZ4Q5Ns", "(II)Lkotlin/ranges/UIntRange;", "rangeUntil", "rangeUntil-WZ4Q5Ns", "rem", "rem-7apg3OU", "rem-WZ4Q5Ns", "rem-VKZWuLQ", "rem-xj2QHRw", "shl", "bitCount", "shl-pVg5ArA", "shr", "shr-pVg5ArA", "times", "times-7apg3OU", "times-WZ4Q5Ns", "times-VKZWuLQ", "times-xj2QHRw", "toByte", "", "toByte-impl", "(I)B", "toDouble", "", "toDouble-impl", "(I)D", "toFloat", "", "toFloat-impl", "(I)F", "toInt", "toInt-impl", "toLong", "", "toLong-impl", "(I)J", "toShort", "", "toShort-impl", "(I)S", "toString", "", "toString-impl", "(I)Ljava/lang/String;", "toUByte", "toUByte-w2LRezQ", "toUInt", "toUInt-pVg5ArA", "toULong", "toULong-s-VKNKU", "toUShort", "toUShort-Mh2AYeg", "xor", "xor-WZ4Q5Ns", "Companion", "kotlin-stdlib"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@JvmInline
/* loaded from: classes.dex */
public final class UInt implements Comparable<UInt> {
    public static final int MAX_VALUE = -1;
    public static final int MIN_VALUE = 0;
    public static final int SIZE_BITS = 32;
    public static final int SIZE_BYTES = 4;
    private final int data;

    /* renamed from: box-impl, reason: not valid java name */
    public static final /* synthetic */ UInt m260boximpl(int i) {
        return new UInt(i);
    }

    /* renamed from: constructor-impl, reason: not valid java name */
    public static int m266constructorimpl(int i) {
        return i;
    }

    /* renamed from: equals-impl, reason: not valid java name */
    public static boolean m272equalsimpl(int i, Object obj) {
        return (obj instanceof UInt) && i == ((UInt) obj).getData();
    }

    /* renamed from: equals-impl0, reason: not valid java name */
    public static final boolean m273equalsimpl0(int i, int i2) {
        return i == i2;
    }

    public static /* synthetic */ void getData$annotations() {
    }

    /* renamed from: hashCode-impl, reason: not valid java name */
    public static int m278hashCodeimpl(int i) {
        return Integer.hashCode(i);
    }

    /* renamed from: toByte-impl, reason: not valid java name */
    private static final byte m306toByteimpl(int i) {
        return (byte) i;
    }

    /* renamed from: toInt-impl, reason: not valid java name */
    private static final int m309toIntimpl(int i) {
        return i;
    }

    /* renamed from: toLong-impl, reason: not valid java name */
    private static final long m310toLongimpl(int i) {
        return i & 4294967295L;
    }

    /* renamed from: toShort-impl, reason: not valid java name */
    private static final short m311toShortimpl(int i) {
        return (short) i;
    }

    /* renamed from: toUInt-pVg5ArA, reason: not valid java name */
    private static final int m314toUIntpVg5ArA(int i) {
        return i;
    }

    public boolean equals(Object obj) {
        return m272equalsimpl(this.data, obj);
    }

    public int hashCode() {
        return m278hashCodeimpl(this.data);
    }

    /* renamed from: unbox-impl, reason: not valid java name and from getter */
    public final /* synthetic */ int getData() {
        return this.data;
    }

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(UInt uInt) {
        return UnsignedKt.uintCompare(getData(), uInt.getData());
    }

    private /* synthetic */ UInt(int i) {
        this.data = i;
    }

    /* renamed from: compareTo-7apg3OU, reason: not valid java name */
    private static final int m261compareTo7apg3OU(int i, byte b) {
        return Integer.compareUnsigned(i, m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: compareTo-xj2QHRw, reason: not valid java name */
    private static final int m265compareToxj2QHRw(int i, short s) {
        return Integer.compareUnsigned(i, m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: compareTo-WZ4Q5Ns, reason: not valid java name */
    private int m263compareToWZ4Q5Ns(int i) {
        return UnsignedKt.uintCompare(getData(), i);
    }

    /* renamed from: compareTo-WZ4Q5Ns, reason: not valid java name */
    private static int m264compareToWZ4Q5Ns(int i, int i2) {
        return UnsignedKt.uintCompare(i, i2);
    }

    /* renamed from: compareTo-VKZWuLQ, reason: not valid java name */
    private static final int m262compareToVKZWuLQ(int i, long j) {
        return Long.compareUnsigned(ULong.m345constructorimpl(i & 4294967295L), j);
    }

    /* renamed from: plus-7apg3OU, reason: not valid java name */
    private static final int m290plus7apg3OU(int i, byte b) {
        return m266constructorimpl(i + m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: plus-xj2QHRw, reason: not valid java name */
    private static final int m293plusxj2QHRw(int i, short s) {
        return m266constructorimpl(i + m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: plus-WZ4Q5Ns, reason: not valid java name */
    private static final int m292plusWZ4Q5Ns(int i, int i2) {
        return m266constructorimpl(i + i2);
    }

    /* renamed from: plus-VKZWuLQ, reason: not valid java name */
    private static final long m291plusVKZWuLQ(int i, long j) {
        return ULong.m345constructorimpl(ULong.m345constructorimpl(i & 4294967295L) + j);
    }

    /* renamed from: minus-7apg3OU, reason: not valid java name */
    private static final int m281minus7apg3OU(int i, byte b) {
        return m266constructorimpl(i - m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: minus-xj2QHRw, reason: not valid java name */
    private static final int m284minusxj2QHRw(int i, short s) {
        return m266constructorimpl(i - m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: minus-WZ4Q5Ns, reason: not valid java name */
    private static final int m283minusWZ4Q5Ns(int i, int i2) {
        return m266constructorimpl(i - i2);
    }

    /* renamed from: minus-VKZWuLQ, reason: not valid java name */
    private static final long m282minusVKZWuLQ(int i, long j) {
        return ULong.m345constructorimpl(ULong.m345constructorimpl(i & 4294967295L) - j);
    }

    /* renamed from: times-7apg3OU, reason: not valid java name */
    private static final int m302times7apg3OU(int i, byte b) {
        return m266constructorimpl(i * m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: times-xj2QHRw, reason: not valid java name */
    private static final int m305timesxj2QHRw(int i, short s) {
        return m266constructorimpl(i * m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: times-WZ4Q5Ns, reason: not valid java name */
    private static final int m304timesWZ4Q5Ns(int i, int i2) {
        return m266constructorimpl(i * i2);
    }

    /* renamed from: times-VKZWuLQ, reason: not valid java name */
    private static final long m303timesVKZWuLQ(int i, long j) {
        return ULong.m345constructorimpl(ULong.m345constructorimpl(i & 4294967295L) * j);
    }

    /* renamed from: div-7apg3OU, reason: not valid java name */
    private static final int m268div7apg3OU(int i, byte b) {
        return Integer.divideUnsigned(i, m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: div-xj2QHRw, reason: not valid java name */
    private static final int m271divxj2QHRw(int i, short s) {
        return Integer.divideUnsigned(i, m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: div-WZ4Q5Ns, reason: not valid java name */
    private static final int m270divWZ4Q5Ns(int i, int i2) {
        return UnsignedKt.m522uintDivideJ1ME1BU(i, i2);
    }

    /* renamed from: div-VKZWuLQ, reason: not valid java name */
    private static final long m269divVKZWuLQ(int i, long j) {
        return Long.divideUnsigned(ULong.m345constructorimpl(i & 4294967295L), j);
    }

    /* renamed from: rem-7apg3OU, reason: not valid java name */
    private static final int m296rem7apg3OU(int i, byte b) {
        return Integer.remainderUnsigned(i, m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: rem-xj2QHRw, reason: not valid java name */
    private static final int m299remxj2QHRw(int i, short s) {
        return Integer.remainderUnsigned(i, m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: rem-WZ4Q5Ns, reason: not valid java name */
    private static final int m298remWZ4Q5Ns(int i, int i2) {
        return UnsignedKt.m523uintRemainderJ1ME1BU(i, i2);
    }

    /* renamed from: rem-VKZWuLQ, reason: not valid java name */
    private static final long m297remVKZWuLQ(int i, long j) {
        return Long.remainderUnsigned(ULong.m345constructorimpl(i & 4294967295L), j);
    }

    /* renamed from: floorDiv-7apg3OU, reason: not valid java name */
    private static final int m274floorDiv7apg3OU(int i, byte b) {
        return Integer.divideUnsigned(i, m266constructorimpl(b & UByte.MAX_VALUE));
    }

    /* renamed from: floorDiv-xj2QHRw, reason: not valid java name */
    private static final int m277floorDivxj2QHRw(int i, short s) {
        return Integer.divideUnsigned(i, m266constructorimpl(s & UShort.MAX_VALUE));
    }

    /* renamed from: floorDiv-WZ4Q5Ns, reason: not valid java name */
    private static final int m276floorDivWZ4Q5Ns(int i, int i2) {
        return Integer.divideUnsigned(i, i2);
    }

    /* renamed from: floorDiv-VKZWuLQ, reason: not valid java name */
    private static final long m275floorDivVKZWuLQ(int i, long j) {
        return Long.divideUnsigned(ULong.m345constructorimpl(i & 4294967295L), j);
    }

    /* renamed from: mod-7apg3OU, reason: not valid java name */
    private static final byte m285mod7apg3OU(int i, byte b) {
        return UByte.m189constructorimpl((byte) Integer.remainderUnsigned(i, m266constructorimpl(b & UByte.MAX_VALUE)));
    }

    /* renamed from: mod-xj2QHRw, reason: not valid java name */
    private static final short m288modxj2QHRw(int i, short s) {
        return UShort.m452constructorimpl((short) Integer.remainderUnsigned(i, m266constructorimpl(s & UShort.MAX_VALUE)));
    }

    /* renamed from: mod-WZ4Q5Ns, reason: not valid java name */
    private static final int m287modWZ4Q5Ns(int i, int i2) {
        return Integer.remainderUnsigned(i, i2);
    }

    /* renamed from: mod-VKZWuLQ, reason: not valid java name */
    private static final long m286modVKZWuLQ(int i, long j) {
        return Long.remainderUnsigned(ULong.m345constructorimpl(i & 4294967295L), j);
    }

    /* renamed from: inc-pVg5ArA, reason: not valid java name */
    private static final int m279incpVg5ArA(int i) {
        return m266constructorimpl(i + 1);
    }

    /* renamed from: dec-pVg5ArA, reason: not valid java name */
    private static final int m267decpVg5ArA(int i) {
        return m266constructorimpl(i - 1);
    }

    /* renamed from: rangeTo-WZ4Q5Ns, reason: not valid java name */
    private static final UIntRange m294rangeToWZ4Q5Ns(int i, int i2) {
        return new UIntRange(i, i2, null);
    }

    /* renamed from: rangeUntil-WZ4Q5Ns, reason: not valid java name */
    private static final UIntRange m295rangeUntilWZ4Q5Ns(int i, int i2) {
        return URangesKt.m1439untilJ1ME1BU(i, i2);
    }

    /* renamed from: shl-pVg5ArA, reason: not valid java name */
    private static final int m300shlpVg5ArA(int i, int i2) {
        return m266constructorimpl(i << i2);
    }

    /* renamed from: shr-pVg5ArA, reason: not valid java name */
    private static final int m301shrpVg5ArA(int i, int i2) {
        return m266constructorimpl(i >>> i2);
    }

    /* renamed from: and-WZ4Q5Ns, reason: not valid java name */
    private static final int m259andWZ4Q5Ns(int i, int i2) {
        return m266constructorimpl(i & i2);
    }

    /* renamed from: or-WZ4Q5Ns, reason: not valid java name */
    private static final int m289orWZ4Q5Ns(int i, int i2) {
        return m266constructorimpl(i | i2);
    }

    /* renamed from: xor-WZ4Q5Ns, reason: not valid java name */
    private static final int m317xorWZ4Q5Ns(int i, int i2) {
        return m266constructorimpl(i ^ i2);
    }

    /* renamed from: inv-pVg5ArA, reason: not valid java name */
    private static final int m280invpVg5ArA(int i) {
        return m266constructorimpl(~i);
    }

    /* renamed from: toUByte-w2LRezQ, reason: not valid java name */
    private static final byte m313toUBytew2LRezQ(int i) {
        return UByte.m189constructorimpl((byte) i);
    }

    /* renamed from: toUShort-Mh2AYeg, reason: not valid java name */
    private static final short m316toUShortMh2AYeg(int i) {
        return UShort.m452constructorimpl((short) i);
    }

    /* renamed from: toULong-s-VKNKU, reason: not valid java name */
    private static final long m315toULongsVKNKU(int i) {
        return ULong.m345constructorimpl(i & 4294967295L);
    }

    /* renamed from: toFloat-impl, reason: not valid java name */
    private static final float m308toFloatimpl(int i) {
        return (float) UnsignedKt.uintToDouble(i);
    }

    /* renamed from: toDouble-impl, reason: not valid java name */
    private static final double m307toDoubleimpl(int i) {
        return UnsignedKt.uintToDouble(i);
    }

    /* renamed from: toString-impl, reason: not valid java name */
    public static String m312toStringimpl(int i) {
        return String.valueOf(i & 4294967295L);
    }

    public String toString() {
        return m312toStringimpl(this.data);
    }
}
