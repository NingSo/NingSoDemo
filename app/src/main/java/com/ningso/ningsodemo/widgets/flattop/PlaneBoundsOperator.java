package com.ningso.ningsodemo.widgets.flattop;

import android.graphics.Rect;

public class PlaneBoundsOperator extends BoundsOperator {
    public PlaneBoundsOperator(float[] ori, Rect rect) {
        super(ori, rect);
    }

    public float getCurrentScaled() {
        return FlattopUtil.getDistanceBetweenPoints(this.mCurrentPoints[0], this.mCurrentPoints[1], this.mCurrentPoints[2], this.mCurrentPoints[3]) / ((float) getInitialWidth());
    }

    public int getInitialWidth() {
        this.mInitialMatrix.getValues(this.mTempValues);
        return (int) (FlattopUtil.getRectFByPoints(this.mOriPoints).width() * this.mTempValues[0]);
    }

    public int getInitialHeight() {
        this.mInitialMatrix.getValues(this.mTempValues);
        return (int) (FlattopUtil.getRectFByPoints(this.mOriPoints).height() * this.mTempValues[0]);
    }
}
