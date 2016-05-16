package com.ningso.ningsodemo.widgets.flattop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ningso.jni.MomiFloodFill;
import com.ningso.ningsodemo.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Flattop extends FrameLayout implements IFlattop {
    private AirTextRequestListener mAirTextListener;
    private List<Aircraft> mAircrafts = new ArrayList();
    private Deck mDeck;

    public interface AirTextRequestListener {
        void onRequestFontFocus(AirText.TextInfo textInfo);

        void onRequestInput(Object obj);
    }

    public Flattop(Context context) {
        super(context);
        init();
    }

    public Flattop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setupDeck();
    }

    private void setupDeck() {
        this.mDeck = new Deck(getContext());
        this.mDeck.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.mDeck);
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.mDeck.setBitmap(bitmap);
        }
    }

    public void addElement(Bitmap bitmap) {
        if (bitmap != null) {
            for (Aircraft craft : this.mAircrafts) {
                craft.land();
            }
            Airplane plane = new Airplane(getContext(), this, bitmap);
            this.mAircrafts.add(plane);
            launchAircraft(plane);
        }
    }

    public void addElement(AirText.TextInfo textInfo) {
        if (!TextUtils.isEmpty(textInfo.text)) {
            AirText at = getFlyingAirText();
            if (at == null) {
                for (Aircraft craft : this.mAircrafts) {
                    craft.land();
                }
                AirText plane = new AirText(getContext(), this, textInfo);
                this.mAircrafts.add(plane);
                launchAircraft(plane);
                requestInput(textInfo);
                requestFontFocus(textInfo);
                return;
            }
            requestFontFocus(textInfo);
            at.reload(textInfo);
        }
    }

    private void launchAircraft(Aircraft craft) {
        craft.setLayoutParams(new LayoutParams(-1, -1));
        addView(craft);
        craft.takeOff(null);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mAircrafts.size() > 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (Aircraft craft : this.mAircrafts) {
                        if (craft.isFlying() && craft.takeOff(event)) {
                            craft.onTouchEvent(event);
                            break;
                        }
                    }
                    int flightNum = 0;
                    for (int i = getChildCount() - 1; i >= 0; i--) {
                        View child = getChildAt(i);
                        if (child instanceof Aircraft) {
                            Aircraft aircraft = (Aircraft) child;
                            if (flightNum >= 1 || !aircraft.takeOff(event)) {
                                aircraft.land();
                            } else {
                                aircraft.onTouchEvent(event);
                                if (child instanceof AirText) {
                                    requestFontFocus(((AirText) child).getTextInfo());
                                }
                                flightNum++;
                            }
                        }
                    }
                    break;
                default:
                    for (Aircraft craft2 : this.mAircrafts) {
                        if (craft2.isFlying()) {
                            craft2.onTouchEvent(event);
                            break;
                        }
                    }
                    break;
            }
        }
        return true;
    }

    private void requestFontFocus(AirText.TextInfo ti) {
        if (this.mAirTextListener != null) {
            this.mAirTextListener.onRequestFontFocus(ti);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public Rect getDeckRect() {
        return this.mDeck.getDeckRect();
    }

    public Matrix getDeckTransformer() {
        return this.mDeck.getDeckTransformer();
    }

    public void removeAircraft(Aircraft aircraft) {
        if (this.mAircrafts.remove(aircraft)) {
            removeView(aircraft);
        }
    }

    public void requestInput(Object obj) {
        if (this.mAirTextListener != null) {
            this.mAirTextListener.onRequestInput(obj);
        }
    }

    public Bitmap getCombinedBitmap(boolean isBlankBackground) {
        Bitmap bitmapBase;
        Bitmap bitmapBackground = this.mDeck.getBitmap();
        if (isBlankBackground) {
            bitmapBase = Bitmap.createBitmap(bitmapBackground.getWidth(), bitmapBackground.getHeight(), bitmapBackground.getConfig());
        } else {
            bitmapBase = bitmapBackground.copy(bitmapBackground.getConfig(), true);
        }
        Canvas canvas = new Canvas(bitmapBase);
        if (isBlankBackground) {
            canvas.drawColor(-1);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof Aircraft) {
                ((Aircraft) child).assemble(canvas);
            }
        }
        if (!isBlankBackground) {
            Bitmap additionalFrame = null;
            try {
                additionalFrame = ImageUtils.loadDisplayAsset(getContext(), "outlinematerial/outline_additional_frame.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (additionalFrame != null) {
                canvas.drawBitmap(additionalFrame, 0.0f, 0.0f, paint);
                additionalFrame.recycle();
            }

        }
        MomiFloodFill.makeBlack(bitmapBase, 100);
        return bitmapBase;
    }

    public Bitmap getCombinedBitmap() {
        return getCombinedBitmap(false);
    }

    public void clean() {
        for (Aircraft craft : this.mAircrafts) {
            removeView(craft);
        }
        this.mAircrafts.clear();
    }

    public void setInputRequestListener(AirTextRequestListener listener) {
        this.mAirTextListener = listener;
    }

    public void dispatchTextInfo(AirText.TextInfo textInfo) {
        AirText at = getFlyingAirText();
        if (at != null) {
            AirText.TextInfo ti = at.getTextInfo();
            ti.text = textInfo.text;
            ti.typeface = textInfo.typeface;
            at.reload(ti);
        }
    }

    private AirText getFlyingAirText() {
        for (Aircraft craft : this.mAircrafts) {
            if ((craft instanceof AirText) && craft.isFlying()) {
                return (AirText) craft;
            }
        }
        return null;
    }

    public AirText.TextInfo getFlyingTextInfo() {
        AirText at = getFlyingAirText();
        if (at == null) {
            return null;
        }
        return at.getTextInfo();
    }
}
