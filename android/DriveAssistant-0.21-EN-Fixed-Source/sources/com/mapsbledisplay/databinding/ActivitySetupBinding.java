package com.mapsbledisplay.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.mapsbledisplay.R;

/* loaded from: classes.dex */
public final class ActivitySetupBinding implements ViewBinding {
    public final Button btnAction;
    public final Button btnBack;
    public final Button btnExtra;
    public final Button btnNext;
    public final Button btnSkip;
    private final LinearLayout rootView;
    public final TextView tvBody;
    public final TextView tvExtra;
    public final TextView tvProgress;
    public final TextView tvStatus;
    public final TextView tvTitle;

    private ActivitySetupBinding(LinearLayout linearLayout, Button button, Button button2, Button button3, Button button4, Button button5, TextView textView, TextView textView2, TextView textView3, TextView textView4, TextView textView5) {
        this.rootView = linearLayout;
        this.btnAction = button;
        this.btnBack = button2;
        this.btnExtra = button3;
        this.btnNext = button4;
        this.btnSkip = button5;
        this.tvBody = textView;
        this.tvExtra = textView2;
        this.tvProgress = textView3;
        this.tvStatus = textView4;
        this.tvTitle = textView5;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivitySetupBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivitySetupBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View inflate = layoutInflater.inflate(R.layout.activity_setup, viewGroup, false);
        if (z) {
            viewGroup.addView(inflate);
        }
        return bind(inflate);
    }

    public static ActivitySetupBinding bind(View view) {
        int i = R.id.btnAction;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            i = R.id.btnBack;
            Button button2 = (Button) ViewBindings.findChildViewById(view, i);
            if (button2 != null) {
                i = R.id.btnExtra;
                Button button3 = (Button) ViewBindings.findChildViewById(view, i);
                if (button3 != null) {
                    i = R.id.btnNext;
                    Button button4 = (Button) ViewBindings.findChildViewById(view, i);
                    if (button4 != null) {
                        i = R.id.btnSkip;
                        Button button5 = (Button) ViewBindings.findChildViewById(view, i);
                        if (button5 != null) {
                            i = R.id.tvBody;
                            TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
                            if (textView != null) {
                                i = R.id.tvExtra;
                                TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                                if (textView2 != null) {
                                    i = R.id.tvProgress;
                                    TextView textView3 = (TextView) ViewBindings.findChildViewById(view, i);
                                    if (textView3 != null) {
                                        i = R.id.tvStatus;
                                        TextView textView4 = (TextView) ViewBindings.findChildViewById(view, i);
                                        if (textView4 != null) {
                                            i = R.id.tvTitle;
                                            TextView textView5 = (TextView) ViewBindings.findChildViewById(view, i);
                                            if (textView5 != null) {
                                                return new ActivitySetupBinding((LinearLayout) view, button, button2, button3, button4, button5, textView, textView2, textView3, textView4, textView5);
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
