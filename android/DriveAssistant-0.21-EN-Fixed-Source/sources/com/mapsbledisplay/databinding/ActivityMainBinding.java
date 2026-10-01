package com.mapsbledisplay.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.mapsbledisplay.R;

/* loaded from: classes.dex */
public final class ActivityMainBinding implements ViewBinding {
    public final Button btnAutostart;
    public final Button btnConnect;
    public final Button btnMenu;
    public final Button btnNotifAccess;
    public final Button btnPair;
    public final Button btnRestricted;
    public final Button btnTest;
    public final TextView dotStep1;
    public final TextView dotStep2;
    public final TextView dotStep3;
    private final ScrollView rootView;
    public final TextView tvAutostart;
    public final TextView tvBleStatus;
    public final TextView tvLastSent;
    public final TextView tvNotifStatus;
    public final TextView tvPairHint;
    public final TextView tvRestricted;

    private ActivityMainBinding(ScrollView scrollView, Button button, Button button2, Button button3, Button button4, Button button5, Button button6, Button button7, TextView textView, TextView textView2, TextView textView3, TextView textView4, TextView textView5, TextView textView6, TextView textView7, TextView textView8, TextView textView9) {
        this.rootView = scrollView;
        this.btnAutostart = button;
        this.btnConnect = button2;
        this.btnMenu = button3;
        this.btnNotifAccess = button4;
        this.btnPair = button5;
        this.btnRestricted = button6;
        this.btnTest = button7;
        this.dotStep1 = textView;
        this.dotStep2 = textView2;
        this.dotStep3 = textView3;
        this.tvAutostart = textView4;
        this.tvBleStatus = textView5;
        this.tvLastSent = textView6;
        this.tvNotifStatus = textView7;
        this.tvPairHint = textView8;
        this.tvRestricted = textView9;
    }

    @Override // androidx.viewbinding.ViewBinding
    public ScrollView getRoot() {
        return this.rootView;
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View inflate = layoutInflater.inflate(R.layout.activity_main, viewGroup, false);
        if (z) {
            viewGroup.addView(inflate);
        }
        return bind(inflate);
    }

    public static ActivityMainBinding bind(View view) {
        int i = R.id.btnAutostart;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            i = R.id.btnConnect;
            Button button2 = (Button) ViewBindings.findChildViewById(view, i);
            if (button2 != null) {
                i = R.id.btnMenu;
                Button button3 = (Button) ViewBindings.findChildViewById(view, i);
                if (button3 != null) {
                    i = R.id.btnNotifAccess;
                    Button button4 = (Button) ViewBindings.findChildViewById(view, i);
                    if (button4 != null) {
                        i = R.id.btnPair;
                        Button button5 = (Button) ViewBindings.findChildViewById(view, i);
                        if (button5 != null) {
                            i = R.id.btnRestricted;
                            Button button6 = (Button) ViewBindings.findChildViewById(view, i);
                            if (button6 != null) {
                                i = R.id.btnTest;
                                Button button7 = (Button) ViewBindings.findChildViewById(view, i);
                                if (button7 != null) {
                                    i = R.id.dotStep1;
                                    TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
                                    if (textView != null) {
                                        i = R.id.dotStep2;
                                        TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                                        if (textView2 != null) {
                                            i = R.id.dotStep3;
                                            TextView textView3 = (TextView) ViewBindings.findChildViewById(view, i);
                                            if (textView3 != null) {
                                                i = R.id.tvAutostart;
                                                TextView textView4 = (TextView) ViewBindings.findChildViewById(view, i);
                                                if (textView4 != null) {
                                                    i = R.id.tvBleStatus;
                                                    TextView textView5 = (TextView) ViewBindings.findChildViewById(view, i);
                                                    if (textView5 != null) {
                                                        i = R.id.tvLastSent;
                                                        TextView textView6 = (TextView) ViewBindings.findChildViewById(view, i);
                                                        if (textView6 != null) {
                                                            i = R.id.tvNotifStatus;
                                                            TextView textView7 = (TextView) ViewBindings.findChildViewById(view, i);
                                                            if (textView7 != null) {
                                                                i = R.id.tvPairHint;
                                                                TextView textView8 = (TextView) ViewBindings.findChildViewById(view, i);
                                                                if (textView8 != null) {
                                                                    i = R.id.tvRestricted;
                                                                    TextView textView9 = (TextView) ViewBindings.findChildViewById(view, i);
                                                                    if (textView9 != null) {
                                                                        return new ActivityMainBinding((ScrollView) view, button, button2, button3, button4, button5, button6, button7, textView, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
