package kotlinx.coroutines.selects;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* compiled from: Select.kt */
@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\u0007"}, d2 = {"Lkotlinx/coroutines/selects/TrySelectDetailedResult;", "", "(Ljava/lang/String;I)V", "SUCCESSFUL", "REREGISTER", "CANCELLED", "ALREADY_SELECTED", "kotlinx-coroutines-core"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class TrySelectDetailedResult {
    private static final /* synthetic */ EnumEntries $ENTRIES;
    private static final /* synthetic */ TrySelectDetailedResult[] $VALUES;
    public static final TrySelectDetailedResult SUCCESSFUL = new TrySelectDetailedResult("SUCCESSFUL", 0);
    public static final TrySelectDetailedResult REREGISTER = new TrySelectDetailedResult("REREGISTER", 1);
    public static final TrySelectDetailedResult CANCELLED = new TrySelectDetailedResult("CANCELLED", 2);
    public static final TrySelectDetailedResult ALREADY_SELECTED = new TrySelectDetailedResult("ALREADY_SELECTED", 3);

    private static final /* synthetic */ TrySelectDetailedResult[] $values() {
        return new TrySelectDetailedResult[]{SUCCESSFUL, REREGISTER, CANCELLED, ALREADY_SELECTED};
    }

    public static EnumEntries<TrySelectDetailedResult> getEntries() {
        return $ENTRIES;
    }

    public static TrySelectDetailedResult valueOf(String str) {
        return (TrySelectDetailedResult) Enum.valueOf(TrySelectDetailedResult.class, str);
    }

    public static TrySelectDetailedResult[] values() {
        return (TrySelectDetailedResult[]) $VALUES.clone();
    }

    private TrySelectDetailedResult(String str, int i) {
    }

    static {
        TrySelectDetailedResult[] $values = $values();
        $VALUES = $values;
        $ENTRIES = EnumEntriesKt.enumEntries($values);
    }
}
