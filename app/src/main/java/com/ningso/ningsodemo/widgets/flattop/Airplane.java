package com.ningso.ningsodemo.widgets.flattop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.ningso.ningsodemo.R;

public class Airplane extends Aircraft {
    private static final float TOUCH_SENSIBILITY = 10.0f;
    private Bitmap mBitmap;
    private PlaneBoundsOperator mBoundsOperator;
    private IFlattop mFlattop;
    private Bitmap mFrameBitmap;
    private boolean mIsFlying;
    private boolean mIsLocated;
    private boolean mIsRotating;
    private Bitmap mLTBitmap;
    private float mLastPolyX;
    private float mLastPolyY;
    private float mLastX = Float.MIN_VALUE;
    private float mLastY = Float.MIN_VALUE;
    private Matrix mMatrix;
    private boolean mMayBeClick;
    private Bitmap mRBBitmap;
    private Bitmap mRTBitmap;
    private Point mRotateCenterPoint;

    public Airplane(Context context, IFlattop mother, Bitmap bitmap) {
        super(context);
        this.mFlattop = mother;
        this.mBitmap = bitmap;
        init();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!this.mIsLocated) {
            initLocation();
        }
        this.mIsLocated = true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(this.mBitmap, getAirway(), paint);
        drawOptionPanel(canvas, paint);
    }

    private void drawOptionPanel(Canvas canvas, Paint paint) {
        if (this.mIsFlying) {
            canvas.drawBitmap(this.mFrameBitmap, getAirway(), paint);
            if (!this.mIsRotating) {
                int w = this.mLTBitmap.getWidth() / 2;
                canvas.drawBitmap(this.mLTBitmap, (float) (this.mBoundsOperator.getConnerPoint(0).x - w), (float) (this.mBoundsOperator.getConnerPoint(0).y - w), paint);
                canvas.drawBitmap(this.mRTBitmap, (float) (this.mBoundsOperator.getConnerPoint(1).x - w), (float) (this.mBoundsOperator.getConnerPoint(1).y - w), paint);
                canvas.drawBitmap(this.mRBBitmap, (float) (this.mBoundsOperator.getConnerPoint(2).x - w), (float) (this.mBoundsOperator.getConnerPoint(2).y - w), paint);
            }
        }
    }

    private void translate(float dx, float dy) {
        Matrix matrix = getAirway();
        matrix.postTranslate(dx, dy);
        if (this.mBoundsOperator.transform(matrix)) {
            invalidate();
        }
    }

    private void doTransferFlip() {
        Bitmap oriBitmap = this.mBitmap.copy(this.mBitmap.getConfig(), true);
        Bitmap bitmap = Bitmap.createBitmap(oriBitmap.getWidth(), oriBitmap.getHeight(), oriBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.postScale(-1.0f, 1.0f, (float) (oriBitmap.getWidth() / 2), (float) (oriBitmap.getHeight() / 2));
        canvas.drawBitmap(oriBitmap, matrix, new Paint());
        this.mBitmap = bitmap;
        invalidate();
    }

    private void doTransferRotate(float x, float y) {
        float[] point = FlattopUtil.getLimitedPoint(new float[]{(float) this.mRotateCenterPoint.x, (float) this.mRotateCenterPoint.y, this.mLastPolyX, this.mLastPolyY, x, y, this.mBoundsOperator.getCurrentScaled()});
        float limitedX = point[0];
        float limitedY = point[1];
        float[] src = new float[]{(float) this.mRotateCenterPoint.x, (float) this.mRotateCenterPoint.y, this.mLastPolyX, this.mLastPolyY};
        float[] dst = new float[]{(float) this.mRotateCenterPoint.x, (float) this.mRotateCenterPoint.y, point[0], point[1]};
        Matrix baseMatrix = getAirway();
        Matrix matrix = new Matrix();
        matrix.setPolyToPoly(src, 0, dst, 0, 2);
        baseMatrix.postConcat(matrix);
        if (this.mBoundsOperator.transform(baseMatrix)) {
            invalidate();
        }
        this.mLastPolyX = limitedX;
        this.mLastPolyY = limitedY;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.mBoundsOperator.isShootingRBButton(event)) {
                    this.mRotateCenterPoint = this.mBoundsOperator.getCenterPoint();
                    this.mLastPolyX = x;
                    this.mLastPolyY = y;
                    this.mIsRotating = true;
                }
                this.mMayBeClick = true;
                this.mLastX = x;
                this.mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (this.mMayBeClick) {
                    if (this.mBoundsOperator.isShootingLTButton(event)) {
                        doTransferFlip();
                    } else if (this.mBoundsOperator.isShootingRTButton(event)) {
                        this.mFlattop.removeAircraft(this);
                    }
                }
                this.mIsRotating = false;
                this.mMayBeClick = false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (this.mLastX == Float.MIN_VALUE) {
                    this.mLastX = x;
                    this.mLastY = y;
                    break;
                }
                float deltaX = x - this.mLastX;
                float deltaY = y - this.mLastY;
                if (Math.abs(deltaX) > TOUCH_SENSIBILITY || Math.abs(deltaY) > TOUCH_SENSIBILITY) {
                    this.mMayBeClick = false;
                }
                if (this.mIsRotating) {
                    doTransferRotate(x, y);
                } else {
                    translate(deltaX, deltaY);
                }
                this.mLastX = x;
                this.mLastY = y;
                break;
        }
        return true;
    }

    public void land() {
        this.mIsFlying = false;
        this.mBoundsOperator.setButtonEnable(false);
        invalidate();
    }

    public boolean isFlying() {
        return this.mIsFlying;
    }

    public boolean takeOff(MotionEvent event) {
        if (event == null) {
            this.mIsFlying = true;
        } else {
            this.mIsFlying = this.mBoundsOperator.isShootingMe(event);
            invalidate();
        }
        if (this.mIsFlying) {
            bringToFront();
            if (this.mBoundsOperator != null) {
                this.mBoundsOperator.setButtonEnable(true);
            }
        }
        return this.mIsFlying;
    }

    public void reload(Object obj) {
    }

    private void init() {
        this.mBoundsOperator = new PlaneBoundsOperator(new float[]{0.0f, 0.0f, (float) this.mBitmap.getWidth(), 0.0f, (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight(), 0.0f, (float) this.mBitmap.getHeight()}, this.mFlattop.getDeckRect());
        this.mLTBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_flip);
        this.mRTBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_delete);
        this.mRBBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_rotate);
        this.mFrameBitmap = FlattopUtil.getNinePatch(R.drawable.bg_element_frame, this.mBitmap.getWidth(), this.mBitmap.getHeight(), getContext());
        this.mBoundsOperator.setButtonRadius(this.mLTBitmap.getWidth() / 2);
        if (this.mIsLocated) {
            initLocation();
        }
    }

    private void initLocation() {
        Matrix matrix = getAirway();
        Rect rect = this.mBoundsOperator.getBoundsRect();
        matrix.postTranslate((float) (((rect.right - rect.left) - this.mBoundsOperator.getInitialWidth()) / 2), (float) (((rect.bottom - rect.top) - this.mBoundsOperator.getInitialHeight()) / 2));
        this.mBoundsOperator.transform(matrix);
    }

    public Matrix getAirway() {
        if (this.mMatrix == null) {
            this.mMatrix = this.mFlattop.getDeckTransformer();
            if (this.mMatrix == null) {
                this.mMatrix = new Matrix();
            } else {
                this.mBoundsOperator.setInitialMatrix(new Matrix(this.mMatrix));
                this.mBoundsOperator.transform(this.mMatrix);
            }
        }
        return this.mMatrix;
    }

    void assemble(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(this.mBitmap, reBuildMatrix(new Matrix(getAirway())), paint);
    }

    private Matrix reBuildMatrix(Matrix matrix) {
        float[] value = new float[9];
        this.mBoundsOperator.getInitialMatrix().getValues(value);
        float ratio = value[0];
        matrix.preScale(1.0f / ratio, 1.0f / ratio);
        matrix.getValues(value);
        float dx = value[2];
        float dy = value[5];
        value[2] = dx / ratio;
        value[5] = dy / ratio;
        matrix.setValues(value);
        return matrix;
    }
}
