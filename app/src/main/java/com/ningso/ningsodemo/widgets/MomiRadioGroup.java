package com.ningso.ningsodemo.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;

import com.ningso.ningsodemo.R;

/**
 * Created by NingSo on 16/4/15.下午11:45
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class MomiRadioGroup extends LinearLayout {

    private int mCheckedId = -1;
    private RadioButtonLayout.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    private boolean mProtectFromCheckedChange = false;

    public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            if (a.hasValue(widthAttr)) {
                this.width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                this.width = -2;
            }
            if (a.hasValue(heightAttr)) {
                this.height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                this.height = -2;
            }
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(MomiRadioGroup momiRadioGroup, int i);
    }

    private class PassThroughHierarchyChangeListener implements OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        private PassThroughHierarchyChangeListener() {
        }

        public void onChildViewAdded(View parent, View child) {
            if (parent == MomiRadioGroup.this) {
                makeCheckListeners(child);
            }
            if (this.mOnHierarchyChangeListener != null) {
                this.mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        public void onChildViewRemoved(View parent, View child) {
            if (parent == MomiRadioGroup.this && (child instanceof RadioButtonLayout)) {
                ((RadioButtonLayout) child).setOnCheckedChangeWidgetListener(null);
            }
            if (this.mOnHierarchyChangeListener != null) {
                this.mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private void makeCheckListeners(View v) {
            if (v instanceof RadioButtonLayout) {
                if (v.getId() == -1) {
                    v.setId(View.generateViewId());
                }
                ((RadioButtonLayout) v).setOnCheckedChangeWidgetListener(MomiRadioGroup.this.mChildOnCheckedChangeListener);
            } else if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                int n = vg.getChildCount();
                for (int i = 0; i < n; i++) {
                    makeCheckListeners(vg.getChildAt(i));
                }
            }
        }
    }

    private class CheckedStateTracker implements RadioButtonLayout.OnCheckedChangeListener {
        private CheckedStateTracker() {
        }

        public void onCheckedChanged(RadioButtonLayout buttonLayout, boolean isChecked) {
            if (!MomiRadioGroup.this.mProtectFromCheckedChange) {
                MomiRadioGroup.this.mProtectFromCheckedChange = true;
                if (MomiRadioGroup.this.mCheckedId != -1) {
                    MomiRadioGroup.this.setCheckedStateForView(MomiRadioGroup.this.mCheckedId, false);
                }
                MomiRadioGroup.this.mProtectFromCheckedChange = false;
                MomiRadioGroup.this.setCheckedId(buttonLayout.getId());
            }
        }
    }

    public MomiRadioGroup(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        init();
    }

    public MomiRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MomiRadioGroup);
        int value = attributes.getResourceId(R.styleable.MomiRadioGroup_checkedButton, -1);
        if (value != -1) {
            this.mCheckedId = value;
        }
        // setOrientation(attributes.getInt(R.styleable.MomiRadioGroup_orientation, LinearLayout.VERTICAL));
        setOrientation(LinearLayout.VERTICAL);
        attributes.recycle();
        init();
    }

    private void init() {
        this.mChildOnCheckedChangeListener = new CheckedStateTracker();
        this.mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(this.mPassThroughListener);
    }

    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        this.mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.mCheckedId != -1) {
            this.mProtectFromCheckedChange = true;
            setCheckedStateForView(this.mCheckedId, true);
            this.mProtectFromCheckedChange = false;
            setCheckedId(this.mCheckedId);
        }
    }

    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        if (child instanceof RadioButtonLayout) {
            RadioButtonLayout button = (RadioButtonLayout) child;
            if (button.isChecked()) {
                this.mProtectFromCheckedChange = true;
                if (this.mCheckedId != -1) {
                    setCheckedStateForView(this.mCheckedId, false);
                }
                this.mProtectFromCheckedChange = false;
                setCheckedId(button.getId());
            }
        }
        super.addView(child, index, params);
    }

    public void check(int id) {
        if (id == -1 || id != this.mCheckedId) {
            if (this.mCheckedId != -1) {
                setCheckedStateForView(this.mCheckedId, false);
            }
            if (id != -1) {
                setCheckedStateForView(id, true);
            }
            setCheckedId(id);
        }
    }

    private void setCheckedId(int id) {
        this.mCheckedId = id;
        if (this.mOnCheckedChangeListener != null) {
            this.mOnCheckedChangeListener.onCheckedChanged(this, this.mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && (checkedView instanceof RadioButtonLayout)) {
            ((RadioButtonLayout) checkedView).setChecked(checked);
        }
    }

    public int getCheckedRadioButtonId() {
        return this.mCheckedId;
    }

    public void clearCheck() {
        check(-1);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected android.widget.LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MomiRadioGroup.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MomiRadioGroup.class.getName());
    }
}
