package com.ningso.ningsodemo.widgets.flattop;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.widget.AutoScrollHelper;
import android.view.MotionEvent;

public class BoundsOperator {
    private Rect mBoundsRect;
    private int mButtonRadius;
    protected float[] mCurrentPoints;
    protected Matrix mInitialMatrix;
    protected float[] mInnerPoints;
    private boolean mIsButtonEnable;
    private boolean mIsFlip = false;
    protected float[] mLastValues = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    protected float[] mOriPoints;
    protected Matrix mTempMatrix = new Matrix();
    protected float[] mTempPoints = new float[8];
    protected float[] mTempValues = new float[9];

    public BoundsOperator(float[] ori, Rect rect) {
        this.mOriPoints = ori;
        this.mBoundsRect = rect;
        this.mInnerPoints = FlattopUtil.getInnerPoints(this.mOriPoints, 3.0f);
        this.mCurrentPoints = FlattopUtil.copyFloatArray(ori);
    }

    public boolean updateTextBounds(Paint paint, String text) {
        float[] oriPoints = FlattopUtil.getTextRectPoints(paint, text);
        float[] innerPoints = FlattopUtil.getInnerPoints(oriPoints, 3.0f);
        this.mTempMatrix.setValues(this.mLastValues);
        this.mTempMatrix.mapPoints(this.mTempPoints, 0, innerPoints, 0, 4);
        if (isOutOfBounds(this.mTempPoints)) {
            return false;
        }
        this.mOriPoints = oriPoints;
        this.mInnerPoints = innerPoints;
        this.mTempMatrix.mapPoints(this.mCurrentPoints, 0, this.mOriPoints, 0, 4);
        return true;
    }

    public void setXFlip() {
        this.mIsFlip = !this.mIsFlip;
    }

    public void setButtonRadius(int radius) {
        this.mButtonRadius = radius;
    }

    public void setButtonEnable(boolean enable) {
        this.mIsButtonEnable = enable;
    }

    public Rect getBoundsRect() {
        return this.mBoundsRect;
    }

    public boolean transform(Matrix matrix) {
        matrix.getValues(this.mTempValues);
        float[] values = FlattopUtil.copyFloatArray(this.mLastValues);
        boolean isChanged = false;
        int i = 0;
        while (i < this.mLastValues.length) {
            if (this.mTempValues[i] != this.mLastValues[i]) {
                values[i] = this.mTempValues[i];
                this.mTempMatrix.setValues(values);
                this.mTempMatrix.mapPoints(this.mTempPoints, 0, this.mInnerPoints, 0, 4);
                if (isOutOfBounds(this.mTempPoints)) {
                    if (i != 2 && i != 5) {
                        isChanged = false;
                        break;
                    }
                    values[i] = this.mLastValues[i];
                } else {
                    isChanged = true;
                }
            }
            i++;
        }
        if (isChanged) {
            matrix.setValues(values);
            for (i = 0; i < values.length; i++) {
                this.mLastValues[i] = values[i];
            }
            matrix.mapPoints(this.mCurrentPoints, 0, this.mOriPoints, 0, 4);
        } else {
            matrix.setValues(this.mLastValues);
        }
        return isChanged;
    }

    private static RectF getOuterRectf(float[] points) {
        float xMax = -2.14748365E9f;
        float xMin = AutoScrollHelper.NO_MAX;
        float yMax = -2.14748365E9f;
        float yMin = AutoScrollHelper.NO_MAX;
        for (int i = 0; i < points.length; i++) {
            if (i % 2 == 0) {
                if (points[i] < xMin) {
                    xMin = points[i];
                }
                if (points[i] > xMax) {
                    xMax = points[i];
                }
            } else {
                if (points[i] < yMin) {
                    yMin = points[i];
                }
                if (points[i] > yMax) {
                    yMax = points[i];
                }
            }
        }
        return new RectF(xMin, yMin, xMax, yMax);
    }

    private boolean isOutOfBounds(float[] points) {
        if (points.length != 8 || this.mBoundsRect == null) {
            return true;
        }
        RectF rf = getOuterRectf(points);
        float tl = rf.left;
        float tt = rf.top;
        float tr = rf.right;
        float tb = rf.bottom;
        int bl = this.mBoundsRect.left;
        int bt = this.mBoundsRect.top;
        int br = this.mBoundsRect.right;
        int bb = this.mBoundsRect.bottom;
        if (tl < ((float) bl) && tr < ((float) bl)) {
            return true;
        }
        if (tr > ((float) br) && tl > ((float) br)) {
            return true;
        }
        if (tt < ((float) bt) && tb < ((float) bt)) {
            return true;
        }
        if (tb <= ((float) bb) || tt <= ((float) bb)) {
            return false;
        }
        return true;
    }

    public boolean isShootingMe(MotionEvent event) {
        if (isShootingArea(event, this.mOriPoints)) {
            return true;
        }
        if (!this.mIsButtonEnable) {
            return false;
        }
        if (isShootingLTButton(event) || isShootingRBButton(event) || isShootingRTButton(event) || isShootingLBButton(event)) {
            return true;
        }
        return false;
    }

    public Point getConnerPoint(int index) {
        int i = 4;
        int i2 = 2;
        int i3 = 1;
        int i4 = 0;
        if (index < 0 || index > 3) {
            return null;
        }
        Point point = new Point();
        float[] fArr;
        float[] fArr2;
        float[] fArr3;
        switch (index) {
            case 0:
                fArr = this.mCurrentPoints;
                if (!this.mIsFlip) {
                    i2 = 0;
                }
                point.x = (int) fArr[i2];
                fArr2 = this.mCurrentPoints;
                if (this.mIsFlip) {
                    i2 = 3;
                } else {
                    i2 = 1;
                }
                point.y = (int) fArr2[i2];
                return point;
            case 1:
                fArr = this.mCurrentPoints;
                if (!this.mIsFlip) {
                    i4 = 2;
                }
                point.x = (int) fArr[i4];
                fArr3 = this.mCurrentPoints;
                if (!this.mIsFlip) {
                    i3 = 3;
                }
                point.y = (int) fArr3[i3];
                return point;
            case 2:
                fArr2 = this.mCurrentPoints;
                if (this.mIsFlip) {
                    i2 = 6;
                } else {
                    i2 = 4;
                }
                point.x = (int) fArr2[i2];
                point.y = (int) this.mCurrentPoints[this.mIsFlip ? 7 : 5];
                return point;
            case 3:
                fArr3 = this.mCurrentPoints;
                if (!this.mIsFlip) {
                    i = 6;
                }
                point.x = (int) fArr3[i];
                point.y = (int) this.mCurrentPoints[this.mIsFlip ? 5 : 7];
                return point;
            default:
                return point;
        }
    }

    public boolean isShootingRTButton(MotionEvent event) {
        return isShootingConnerButton(getConnerPoint(1), event);
    }

    public boolean isShootingLBButton(MotionEvent event) {
        return isShootingConnerButton(getConnerPoint(3), event);
    }

    public boolean isShootingRBButton(MotionEvent event) {
        return isShootingConnerButton(getConnerPoint(2), event);
    }

    public boolean isShootingLTButton(MotionEvent event) {
        return isShootingConnerButton(getConnerPoint(0), event);
    }

    private boolean isShootingConnerButton(Point point, MotionEvent event) {
        return new RectF((float) (point.x - this.mButtonRadius), (float) (point.y - this.mButtonRadius), (float) (point.x + this.mButtonRadius), (float) (point.y + this.mButtonRadius)).contains(event.getX(), event.getY());
    }

    private boolean isShootingArea(MotionEvent event, float[] points) {
        float x = event.getX();
        float y = event.getY();
        this.mTempMatrix.setValues(this.mLastValues);
        Matrix inverse = new Matrix();
        this.mTempMatrix.invert(inverse);
        //// TODO: 16/4/16  dst = new float[2];
        float[] dst = new float[2];
        inverse.mapPoints(dst, new float[]{x, y});
        return FlattopUtil.getRectFByPoints(points).contains(dst[0], dst[1]);
    }

    public float[] getCurrentPoints() {
        return this.mCurrentPoints;
    }

    public float[] getCurrentInner() {
        this.mTempMatrix.setValues(this.mLastValues);
        this.mTempMatrix.mapPoints(this.mTempPoints, 0, this.mInnerPoints, 0, 4);
        return this.mTempPoints;
    }

    public Point getCenterPoint() {
        float x = FlattopUtil.getRectFByPoints(this.mOriPoints).centerX();
        float y = FlattopUtil.getRectFByPoints(this.mOriPoints).centerY();
        this.mTempMatrix.setValues(this.mLastValues);
        this.mTempMatrix.mapPoints(this.mTempPoints, 0, new float[]{x, y}, 0, 1);
        Point p = new Point();
        p.set((int) this.mTempPoints[0], (int) this.mTempPoints[1]);
        return p;
    }

    public void setInitialMatrix(Matrix matrix) {
        this.mInitialMatrix = new Matrix(matrix);
    }

    public Matrix getInitialMatrix() {
        return this.mInitialMatrix;
    }
}
