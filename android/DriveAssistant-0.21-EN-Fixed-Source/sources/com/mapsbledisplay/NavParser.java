package com.mapsbledisplay;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.EnvironmentCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* compiled from: NavParser.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002J\u0010\u0010\n\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002J$\u0010\u000b\u001a\u00020\u00072\b\u0010\f\u001a\u0004\u0018\u00010\u00072\b\u0010\r\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000e\u001a\u00020\u0007H\u0002J$\u0010\u000f\u001a\u00020\u00102\b\u0010\f\u001a\u0004\u0018\u00010\u00072\b\u0010\r\u001a\u0004\u0018\u00010\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/mapsbledisplay/NavParser;", "", "()V", "distanceRegex", "Lkotlin/text/Regex;", "streetMarkers", "", "", "cleanStreet", "s", "detectManeuver", "extractStreet", "title", "text", "all", "parse", "Lcom/mapsbledisplay/NavData;", "sub", "app_release"}, k = 1, mv = {1, 9, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class NavParser {
    public static final NavParser INSTANCE = new NavParser();
    private static final Regex distanceRegex = new Regex("(\\d+(?:[.,]\\d+)?)[\\s\\u00a0\\u202f\\u2007\\u2009]*(km|m|mi|ft|yd)\\b", RegexOption.IGNORE_CASE);
    private static final List<String> streetMarkers = CollectionsKt.listOf((Object[]) new String[]{" auf ", " onto ", " on ", " Richtung ", " richtung ", " toward ", " towards "});

    private NavParser() {
    }

    public final NavData parse(String title, String text, String sub) {
        String value;
        String replace$default;
        List listOfNotNull = CollectionsKt.listOfNotNull((Object[]) new String[]{title, text});
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(listOfNotNull, 10));
        Iterator it = listOfNotNull.iterator();
        while (it.hasNext()) {
            arrayList.add(StringsKt.trim((CharSequence) it.next()).toString());
        }
        ArrayList arrayList2 = new ArrayList();
        for (Object obj : arrayList) {
            if (((String) obj).length() > 0) {
                arrayList2.add(obj);
            }
        }
        String joinToString$default = CollectionsKt.joinToString$default(arrayList2, "  ", null, null, 0, null, null, 62, null);
        String str = joinToString$default;
        if (StringsKt.isBlank(str)) {
            return NavData.INSTANCE.getEMPTY();
        }
        String detectManeuver = detectManeuver(joinToString$default);
        String str2 = null;
        MatchResult find$default = Regex.find$default(distanceRegex, str, 0, 2, null);
        if (find$default != null && (value = find$default.getValue()) != null && (replace$default = StringsKt.replace$default(value, " ", " ", false, 4, (Object) null)) != null) {
            str2 = StringsKt.trim((CharSequence) replace$default).toString();
        }
        if (str2 == null) {
            str2 = "< 50 m";
        }
        return new NavData(detectManeuver, str2, extractStreet(title, text, joinToString$default), joinToString$default);
    }

    private final String detectManeuver(String s) {
        String lowerCase = s.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        String str = lowerCase;
        if (StringsKt.contains$default((CharSequence) str, (CharSequence) "wenden", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "u-turn", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "u turn", false, 2, (Object) null)) {
            return "uturn";
        }
        String str2 = "roundabout";
        if (!StringsKt.contains$default((CharSequence) str, (CharSequence) "kreisverkehr", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "you have arrived", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "rotary", false, 2, (Object) null)) {
            str2 = "arrive";
            if (!StringsKt.contains$default((CharSequence) str, (CharSequence) "ziel", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "angekommen", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "you have arrived", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "destination", false, 2, (Object) null)) {
                str2 = "merge";
                if (!StringsKt.contains$default((CharSequence) str, (CharSequence) "auffahr", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "einfaedel", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "einfädel", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "you have arrived", false, 2, (Object) null)) {
                    if (StringsKt.contains$default((CharSequence) str, (CharSequence) "scharf rechts", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "sharp right", false, 2, (Object) null)) {
                        return "sharp-right";
                    }
                    if (StringsKt.contains$default((CharSequence) str, (CharSequence) "scharf links", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "sharp left", false, 2, (Object) null)) {
                        return "sharp-left";
                    }
                    boolean z = true;
                    boolean z2 = StringsKt.contains$default((CharSequence) str, (CharSequence) "leicht rechts", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "slight right", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "halten sie sich rechts", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "keep right", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "rechts halten", false, 2, (Object) null);
                    if (!StringsKt.contains$default((CharSequence) str, (CharSequence) "leicht links", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "slight left", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "halten sie sich links", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "keep left", false, 2, (Object) null) && !StringsKt.contains$default((CharSequence) str, (CharSequence) "links halten", false, 2, (Object) null)) {
                        z = false;
                    }
                    if (z2) {
                        return "slight-right";
                    }
                    if (z) {
                        return "slight-left";
                    }
                    return (StringsKt.contains$default((CharSequence) str, (CharSequence) "rechts abbiegen", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "turn right", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "nach rechts", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "right onto", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "right on", false, 2, (Object) null)) ? "turn-right" : (StringsKt.contains$default((CharSequence) str, (CharSequence) "links abbiegen", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "turn left", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "nach links", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "left onto", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "left on", false, 2, (Object) null)) ? "turn-left" : (StringsKt.contains$default((CharSequence) str, (CharSequence) "geradeaus", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "straight", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "weiter", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "continue", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "head", false, 2, (Object) null)) ? "straight" : new Regex("\\brechts\\b|\\bright\\b").containsMatchIn(str) ? "turn-right" : new Regex("\\blinks\\b|\\bleft\\b").containsMatchIn(str) ? "turn-left" : EnvironmentCompat.MEDIA_UNKNOWN;
                }
            }
        }
        return str2;
    }

    private final String extractStreet(String title, String text, String all) {
        for (String str : CollectionsKt.listOfNotNull((Object[]) new String[]{title, text})) {
            for (String str2 : streetMarkers) {
                int indexOf$default = StringsKt.indexOf$default((CharSequence) str, str2, 0, true, 2, (Object) null);
                if (indexOf$default >= 0) {
                    String substring = str.substring(indexOf$default + str2.length());
                    Intrinsics.checkNotNullExpressionValue(substring, "substring(...)");
                    String obj = StringsKt.trim((CharSequence) substring).toString();
                    if (obj.length() > 0) {
                        return cleanStreet(obj);
                    }
                }
            }
        }
        String obj2 = text != null ? StringsKt.trim((CharSequence) distanceRegex.replace(text, "")).toString() : null;
        return cleanStreet(obj2 != null ? obj2 : "");
    }

    private final String cleanStreet(String s) {
        return StringsKt.take(StringsKt.trim(StringsKt.trim((CharSequence) new Regex("\\s+").replace(s, " ")).toString(), '-', Typography.middleDot, Typography.bullet, ',', '.'), 60);
    }
}
