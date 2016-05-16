package com.ningso.jni;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

/**
 * Created by NingSo on 16/4/16.上午12:54
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class MomiFloodFill {
    private int[] mPainting;

    public static native void eraseBackground(int[] iArr, int[] iArr2, int i, int i2);

    public static native String getGooglePlayBillingSecret();

    public static native String getSaltSecret();

    public static native String getSaltSecretPass();

    public static native String getSecret();

    public static native void makeBlack(int[] iArr, int i, int i2, int i3);

    public native void fill(int i, int i2, int i3);

    public native void release();

    public native void setup(Context context, int[] iArr, int[] iArr2, int i, int i2);

    public native void undo();

    static {
        System.loadLibrary("momi-jni");
    }

    public void setup(Context cx, Bitmap bm, Bitmap template) {
        int w = bm.getWidth();
        int h = bm.getHeight();
        this.mPainting = new int[(w * h)];
        int[] outline = new int[(w * h)];
        bm.getPixels(this.mPainting, 0, w, 0, 0, w, h);
        template.getPixels(outline, 0, w, 0, 0, w, h);
        release();
        setup(cx, outline, this.mPainting, w, h);
        template.recycle();
    }

    public static void makeBlack(Bitmap bm, int v) {
        int w = bm.getWidth();
        int h = bm.getHeight();
        int[] outline = new int[(w * h)];
        bm.getPixels(outline, 0, w, 0, 0, w, h);
        makeBlack(outline, w, h, v);
        bm.setPixels(outline, 0, w, 0, 0, w, h);
    }

    public void updateBitmap(Bitmap bm) {
        int w = bm.getWidth();
        Bitmap bitmap = bm;
        bitmap.setPixels(this.mPainting, 0, w, 0, 0, w, bm.getHeight());
    }

    public static void eraseBackground(Bitmap outline, Bitmap painting) {
        if (Build.VERSION.SDK_INT >= 12) {
            painting.setHasAlpha(true);
        }
        int w = painting.getWidth();
        int h = painting.getHeight();
        int[] paintingPixels = new int[(w * h)];
        int[] outlinePixels = new int[(w * h)];
        painting.getPixels(paintingPixels, 0, w, 0, 0, w, h);
        outline.getPixels(outlinePixels, 0, w, 0, 0, w, h);
        eraseBackground(outlinePixels, paintingPixels, w, h);
        painting.setPixels(paintingPixels, 0, w, 0, 0, w, h);
    }
}
