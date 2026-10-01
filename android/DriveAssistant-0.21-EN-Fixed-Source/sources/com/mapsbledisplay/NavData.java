package com.mapsbledisplay;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: NavData.kt */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\b\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J1\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001J\u0006\u0010\u0017\u001a\u00020\u0013J\u0006\u0010\u0018\u001a\u00020\u0003J\t\u0010\u0019\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u001b"}, d2 = {"Lcom/mapsbledisplay/NavData;", "", "maneuver", "", "distance", "street", "raw", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDistance", "()Ljava/lang/String;", "getManeuver", "getRaw", "getStreet", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "isEmpty", "toPayload", "toString", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final /* data */ class NavData {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final NavData EMPTY = new NavData("clear", "", "", null, 8, null);
    private final String distance;
    private final String maneuver;
    private final String raw;
    private final String street;

    public static /* synthetic */ NavData copy$default(NavData navData, String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 1) != 0) {
            str = navData.maneuver;
        }
        if ((i & 2) != 0) {
            str2 = navData.distance;
        }
        if ((i & 4) != 0) {
            str3 = navData.street;
        }
        if ((i & 8) != 0) {
            str4 = navData.raw;
        }
        return navData.copy(str, str2, str3, str4);
    }

    /* renamed from: component1, reason: from getter */
    public final String getManeuver() {
        return this.maneuver;
    }

    /* renamed from: component2, reason: from getter */
    public final String getDistance() {
        return this.distance;
    }

    /* renamed from: component3, reason: from getter */
    public final String getStreet() {
        return this.street;
    }

    /* renamed from: component4, reason: from getter */
    public final String getRaw() {
        return this.raw;
    }

    public final NavData copy(String maneuver, String distance, String street, String raw) {
        Intrinsics.checkNotNullParameter(maneuver, "maneuver");
        Intrinsics.checkNotNullParameter(distance, "distance");
        Intrinsics.checkNotNullParameter(street, "street");
        Intrinsics.checkNotNullParameter(raw, "raw");
        return new NavData(maneuver, distance, street, raw);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NavData)) {
            return false;
        }
        NavData navData = (NavData) other;
        return Intrinsics.areEqual(this.maneuver, navData.maneuver) && Intrinsics.areEqual(this.distance, navData.distance) && Intrinsics.areEqual(this.street, navData.street) && Intrinsics.areEqual(this.raw, navData.raw);
    }

    public int hashCode() {
        return (((((this.maneuver.hashCode() * 31) + this.distance.hashCode()) * 31) + this.street.hashCode()) * 31) + this.raw.hashCode();
    }

    public String toString() {
        return "NavData(maneuver=" + this.maneuver + ", distance=" + this.distance + ", street=" + this.street + ", raw=" + this.raw + ")";
    }

    public NavData(String maneuver, String distance, String street, String raw) {
        Intrinsics.checkNotNullParameter(maneuver, "maneuver");
        Intrinsics.checkNotNullParameter(distance, "distance");
        Intrinsics.checkNotNullParameter(street, "street");
        Intrinsics.checkNotNullParameter(raw, "raw");
        this.maneuver = maneuver;
        this.distance = distance;
        this.street = street;
        this.raw = raw;
    }

    public final String getManeuver() {
        return this.maneuver;
    }

    public final String getDistance() {
        return this.distance;
    }

    public final String getStreet() {
        return this.street;
    }

    public /* synthetic */ NavData(String str, String str2, String str3, String str4, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, (i & 8) != 0 ? "" : str4);
    }

    public final String getRaw() {
        return this.raw;
    }

    public final String toPayload() {
        return this.maneuver + "|" + NavDataKt.toDisplayAscii(this.distance) + "|" + NavDataKt.toDisplayAscii(this.street);
    }

    public final boolean isEmpty() {
        return StringsKt.isBlank(this.maneuver) && StringsKt.isBlank(this.distance) && StringsKt.isBlank(this.street);
    }

    /* compiled from: NavData.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Lcom/mapsbledisplay/NavData$Companion;", "", "()V", "EMPTY", "Lcom/mapsbledisplay/NavData;", "getEMPTY", "()Lcom/mapsbledisplay/NavData;", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final NavData getEMPTY() {
            return NavData.EMPTY;
        }
    }
}
