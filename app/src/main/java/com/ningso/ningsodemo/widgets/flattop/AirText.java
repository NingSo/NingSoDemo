package com.ningso.ningsodemo.widgets.flattop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;

import com.ningso.ningsodemo.R;
import com.xinmei365.fontsdk.bean.Font;

import java.util.Vector;

public class AirText extends Aircraft {
    private static final float TOUCH_SENSIBILITY = 10.0f;
    private BoundsOperator mBoundsOperator;
    private IFlattop mFlattop;
    private boolean mIsFlying;
    private boolean mIsLocated;
    private boolean mIsRotating;
    private Bitmap mLBBitmap;
    private Bitmap mLTBitmap;
    private float mLastPolyScale = 1.0f;
    private float mLastX = Float.MIN_VALUE;
    private float mLastY = Float.MIN_VALUE;
    private Matrix mMatrix;
    private boolean mMayBeClick;
    private Paint mPaint;
    private float mPolyXOffset;
    private float mPolyYOffset;
    private Bitmap mRBBitmap;
    private Bitmap mRTBitmap;
    private Point mRotateCenterPoint;
    private String mText;
    private TextInfo mTextInfo;
    private float mTextSize = 90.0f;

    public static class TextInfo {
        public Font font;
        public String text;
        public Typeface typeface;
    }

    public AirText(Context context, IFlattop mother, TextInfo textInfo) {
        super(context);
        this.mFlattop = mother;
        initPaint();
        this.mTextInfo = textInfo;
        this.mText = textInfo.text;
        this.mPaint.setTypeface(textInfo.typeface);
        init();
    }

    private void initPaint() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setFakeBoldText(true);
        this.mPaint.setDither(true);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setStrokeWidth(2.0f);
        this.mPaint.setTextAlign(Align.CENTER);
        this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
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
        drawMyText(canvas, this.mPaint, this.mText, getAirway());
        drawOptionPanel(canvas, new Paint());
    }

    public static void drawMyText(Canvas canvas, Paint paint, String text, Matrix matrix) {
        Rect boundsRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), boundsRect);
        int h = boundsRect.height();
        int b = boundsRect.height() - boundsRect.bottom;
        Vector<String> textLines = FlattopUtil.getTextLinesVector(text);
        int initialY = -(FlattopUtil.getTextRect(paint, text).height() / 2);
        if (matrix != null) {
            canvas.save();
            canvas.concat(matrix);
            for (int i = 0; i < textLines.size(); i++) {
                String str = (String) textLines.get(i);
                paint.setStyle(Style.FILL);
                paint.setColor(-1);
                canvas.drawText(str.trim(), 0.0f, (float) (initialY + b), paint);
                paint.setStyle(Style.STROKE);
                paint.setColor(ViewCompat.MEASURED_STATE_MASK);
                canvas.drawText(str.trim(), 0.0f, (float) (initialY + b), paint);
                initialY += h;
            }
            canvas.restore();
        }
    }

    private void drawOptionPanel(Canvas canvas, Paint paint) {
        if (this.mIsFlying) {
            float[] points = this.mBoundsOperator.getCurrentPoints();
            Path path = new Path();
            path.moveTo(points[0], points[1]);
            path.lineTo(points[2], points[3]);
            path.lineTo(points[4], points[5]);
            path.lineTo(points[6], points[7]);
            path.lineTo(points[0], points[1]);
            Paint pathPaint = new Paint();
            pathPaint.setAntiAlias(true);
            pathPaint.setDither(true);
            pathPaint.setStyle(Style.STROKE);
            pathPaint.setColor(SupportMenu.CATEGORY_MASK);
            canvas.drawPath(path, pathPaint);
            if (!this.mIsRotating) {
                int w = this.mLTBitmap.getWidth() / 2;
                canvas.drawBitmap(this.mLTBitmap, (float) (this.mBoundsOperator.getConnerPoint(0).x - w), (float) (this.mBoundsOperator.getConnerPoint(0).y - w), paint);
                canvas.drawBitmap(this.mRTBitmap, (float) (this.mBoundsOperator.getConnerPoint(1).x - w), (float) (this.mBoundsOperator.getConnerPoint(1).y - w), paint);
                canvas.drawBitmap(this.mRBBitmap, (float) (this.mBoundsOperator.getConnerPoint(2).x - w), (float) (this.mBoundsOperator.getConnerPoint(2).y - w), paint);
                canvas.drawBitmap(this.mLBBitmap, (float) (this.mBoundsOperator.getConnerPoint(3).x - w), (float) (this.mBoundsOperator.getConnerPoint(3).y - w), paint);
            }
        }
    }

    void land() {
        this.mIsFlying = false;
        this.mBoundsOperator.setButtonEnable(false);
        invalidate();
    }

    boolean isFlying() {
        return this.mIsFlying;
    }

    boolean takeOff(MotionEvent event) {
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

    void reload(Object obj) {
        TextInfo textInfo = (TextInfo) obj;
        this.mTextInfo = textInfo;
        this.mText = textInfo.text;
        this.mPaint.setTypeface(textInfo.typeface);
        if (this.mBoundsOperator.updateTextBounds(this.mPaint, this.mText)) {
            invalidate();
        } else {
            invalidate();
        }
    }

    private void init() {
        this.mBoundsOperator = new BoundsOperator(FlattopUtil.getTextRectPoints(this.mPaint, this.mText), this.mFlattop.getDeckRect());
        this.mLTBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_flip);
        this.mRTBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_delete);
        this.mRBBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_rotate);
        this.mLBBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.craft_edit);
        this.mBoundsOperator.setButtonRadius(this.mLTBitmap.getWidth() / 2);
        if (this.mIsLocated) {
            initLocation();
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
        Matrix matrix = getAirway();
        matrix.preScale(-1.0f, 1.0f);
        if (this.mBoundsOperator.transform(matrix)) {
            this.mBoundsOperator.setXFlip();
            invalidate();
        }
    }

    private void doTransferRotate(float x, float y) {
        Point rbPoint = this.mBoundsOperator.getConnerPoint(2);
        float[] point = FlattopUtil.getLimitedPointOnlyRotate(new float[]{(float) this.mRotateCenterPoint.x, (float) this.mRotateCenterPoint.y, (float) rbPoint.x, (float) rbPoint.y, x, y});
        float limitedX = point[0];
        float limitedY = point[1];
        float[] src = new float[]{(float) this.mRotateCenterPoint.x, (float) this.mRotateCenterPoint.y, (float) rbPoint.x, (float) rbPoint.y};
        float[] dst = new float[]{(float) this.mRotateCenterPoint.x, (float) this.mRotateCenterPoint.y, limitedX, limitedY};
        Matrix baseMatrix = getAirway();
        Matrix matrix = new Matrix();
        matrix.setPolyToPoly(src, 0, dst, 0, 2);
        baseMatrix.postConcat(matrix);
        float tempScale = this.mLastPolyScale * point[2];
        Paint p = new Paint(this.mPaint);
        p.setTextSize(this.mTextSize * tempScale);
        if (tempScale >= 1.0f && this.mBoundsOperator.updateTextBounds(p, this.mText)) {
            this.mPaint = p;
            this.mLastPolyScale = tempScale;
        }
        if (this.mBoundsOperator.transform(baseMatrix)) {
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.mBoundsOperator.isShootingRBButton(event)) {
                    this.mRotateCenterPoint = this.mBoundsOperator.getCenterPoint();
                    Point rbPoint = this.mBoundsOperator.getConnerPoint(2);
                    this.mPolyXOffset = x - ((float) rbPoint.x);
                    this.mPolyYOffset = y - ((float) rbPoint.y);
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
                    } else if (this.mBoundsOperator.isShootingLBButton(event)) {
                        this.mFlattop.requestInput(this.mTextInfo);
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
                    doTransferRotate(x - this.mPolyXOffset, y - this.mPolyYOffset);
                } else {
                    translate(deltaX, deltaY);
                }
                this.mLastX = x;
                this.mLastY = y;
                break;
        }
        return true;
    }

    private void initLocation() {
        Matrix matrix = getAirway();
        Rect rect = this.mBoundsOperator.getBoundsRect();
        matrix.postTranslate((float) ((rect.right - rect.left) / 2), (float) ((rect.bottom - rect.top) / 2));
        this.mBoundsOperator.transform(matrix);
    }

    Matrix getAirway() {
        if (this.mMatrix == null) {
            this.mMatrix = this.mFlattop.getDeckTransformer();
            if (this.mMatrix == null) {
                this.mMatrix = new Matrix();
            } else {
                this.mBoundsOperator.setInitialMatrix(this.mMatrix);
                this.mBoundsOperator.transform(this.mMatrix);
            }
        }
        return this.mMatrix;
    }

    void assemble(Canvas canvas) {
        drawMyText(canvas, this.mPaint, this.mText, reBuildMatrix(new Matrix(getAirway())));
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

    public TextInfo getTextInfo() {
        return this.mTextInfo;
    }
}
