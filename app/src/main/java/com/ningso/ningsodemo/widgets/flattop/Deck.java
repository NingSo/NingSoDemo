package com.ningso.ningsodemo.widgets.flattop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Deck extends View {
    private Bitmap mBitmap;
    private boolean mIsLocated;
    private Matrix mMatrix;
    private Rect mRect;

    public Deck(Context context) {
        super(context);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mRect = new Rect(left, top, right, bottom);
        if (!(this.mIsLocated || this.mBitmap == null)) {
            this.mIsLocated = true;
            setBitmap(this.mBitmap);
        }
        this.mIsLocated = true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mBitmap != null && this.mMatrix != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawBitmap(this.mBitmap, this.mMatrix, paint);
        }
    }

    public Rect getDeckRect() {
        return new Rect(this.mRect);
    }

    public Matrix getDeckTransformer() {
        return new Matrix(this.mMatrix);
    }

    public boolean setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if (!this.mIsLocated) {
            return false;
        }
        this.mMatrix = new Matrix();
        float scaleRatio = ((float) (this.mRect.right - this.mRect.left)) / ((float) bitmap.getWidth());
        this.mMatrix.setScale(scaleRatio, scaleRatio);
        invalidate();
        return true;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }
}
