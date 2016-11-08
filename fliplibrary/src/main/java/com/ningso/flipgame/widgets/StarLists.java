package com.ningso.flipgame.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class StarLists extends LinearLayout {
    public StarLists(Context context) {
        super(context);
    }

    public StarLists(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StarLists(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.getChildCount() != 3) {
            super.onLayout(changed, l, t, r, b);
        } else {
            this.layoutOne(this.getChildAt(0), r - l, b - t, 25);
            this.layoutOne(this.getChildAt(1), r - l, b - t, 50);
            this.layoutOne(this.getChildAt(2), r - l, b - t, 75);
        }
    }

    private void layoutOne(View one, int width, int height, int percent) {
        one.layout(width * percent / 100 - one.getMeasuredWidth() / 2,
                (height - one.getMeasuredHeight()) / 2,
                width * percent / 100 + one.getMeasuredWidth() / 2,
                height - (height - one.getMeasuredHeight()) / 2);
    }
}