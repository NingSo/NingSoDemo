package com.ningso.ningsodemo.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class FitSystemWindowRelativeLayout extends RelativeLayout {
    private int insetsBottom;

    public FitSystemWindowRelativeLayout(Context context) {
        this(context, null);
    }

    public FitSystemWindowRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitSystemWindowRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.insetsBottom = 0;
        setFitsSystemWindows(true);
    }

    protected boolean fitSystemWindows(Rect insets) {
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }

    public int getInsetsBottom() {
        return this.insetsBottom;
    }
}
