package com.ningso.ningsodemo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

/**
 * Created by NingSo on 16/4/15.下午11:48
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class RadioButtonLayout extends RelativeLayout {
    private boolean isChecked;
    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioButtonLayout radioButtonLayout, boolean z);
    }

    public RadioButtonLayout(Context context) {
        super(context);
        init();
    }

    public RadioButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioButtonLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClickable(true);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public boolean performClick() {
        setChecked(true);
        return super.performClick();
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            setAllRadioButtonsChecked(checked);
            if (!this.mBroadcasting) {
                this.mBroadcasting = true;
                if (this.mOnCheckedChangeWidgetListener != null) {
                    this.mOnCheckedChangeWidgetListener.onCheckedChanged(this, checked);
                }
                this.mBroadcasting = false;
            }
        }
    }

    private void setAllRadioButtonsChecked(boolean checked) {
        setAllRadioButtonsChecked(this, checked);
    }

    private void setAllRadioButtonsChecked(ViewGroup vg, boolean checked) {
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof ViewGroup) {
                setAllRadioButtonsChecked((ViewGroup) v, checked);
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setChecked(checked);
            }
        }
    }

    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeWidgetListener = listener;
    }
}
