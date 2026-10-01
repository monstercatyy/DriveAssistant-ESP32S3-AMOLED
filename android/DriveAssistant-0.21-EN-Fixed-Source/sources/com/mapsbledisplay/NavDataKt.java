package com.mapsbledisplay;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: NavData.kt */
@Metadata(d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0001H\u0000¨\u0006\u0002"}, d2 = {"toDisplayAscii", "", "app_release"}, k = 2, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class NavDataKt {
    public static final String toDisplayAscii(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == 228) {
                sb.append("ae");
            } else if (charAt == 246) {
                sb.append("oe");
            } else if (charAt == 252) {
                sb.append("ue");
            } else if (charAt == 196) {
                sb.append("Ae");
            } else if (charAt == 214) {
                sb.append("Oe");
            } else if (charAt == 220) {
                sb.append("Ue");
            } else if (charAt == 223) {
                sb.append("ss");
            } else if (charAt == 233 || charAt == 232 || charAt == 234) {
                sb.append('e');
            } else if (charAt == 225 || charAt == 224 || charAt == 226) {
                sb.append('a');
            } else if (charAt == 160 || charAt == 8194 || charAt == 8195 || charAt == 8199 || charAt == 8201 || charAt == 8202 || charAt == 8239) {
                sb.append(' ');
            } else if (charAt == 8211 || charAt == 8212) {
                sb.append('-');
            } else if (charAt == 183 || charAt == 8226) {
                sb.append('.');
            } else if (' ' <= charAt && charAt < 127) {
                sb.append(charAt);
            }
        }
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "toString(...)");
        return StringsKt.trim((CharSequence) sb2).toString();
    }
}
