package kotlinx.coroutines.channels;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* compiled from: BufferOverflow.kt */
@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"}, d2 = {"Lkotlinx/coroutines/channels/BufferOverflow;", "", "(Ljava/lang/String;I)V", "SUSPEND", "DROP_OLDEST", "DROP_LATEST", "kotlinx-coroutines-core"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class BufferOverflow {
    private static final /* synthetic */ EnumEntries $ENTRIES;
    private static final /* synthetic */ BufferOverflow[] $VALUES;
    public static final BufferOverflow SUSPEND = new BufferOverflow("SUSPEND", 0);
    public static final BufferOverflow DROP_OLDEST = new BufferOverflow("DROP_OLDEST", 1);
    public static final BufferOverflow DROP_LATEST = new BufferOverflow("DROP_LATEST", 2);

    private static final /* synthetic */ BufferOverflow[] $values() {
        return new BufferOverflow[]{SUSPEND, DROP_OLDEST, DROP_LATEST};
    }

    public static EnumEntries<BufferOverflow> getEntries() {
        return $ENTRIES;
    }

    public static BufferOverflow valueOf(String str) {
        return (BufferOverflow) Enum.valueOf(BufferOverflow.class, str);
    }

    public static BufferOverflow[] values() {
        return (BufferOverflow[]) $VALUES.clone();
    }

    private BufferOverflow(String str, int i) {
    }

    static {
        BufferOverflow[] $values = $values();
        $VALUES = $values;
        $ENTRIES = EnumEntriesKt.enumEntries($values);
    }
}
