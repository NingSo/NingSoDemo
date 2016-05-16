package com.ningso.ningsodemo.widgets.flattop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;

import java.util.Iterator;
import java.util.Vector;

public class FlattopUtil {
    private static final float MAX_SCALE = 1.4f;
    private static final float MIN_SCALE = 1.0f;

    public static Bitmap getNinePatch(int id, int x, int y, Context context) {
        NinePatchDrawable np_drawable = new NinePatchDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), id), BitmapFactory.decodeResource(context.getResources(), id).getNinePatchChunk(), new Rect(), null);
        np_drawable.setBounds(0, 0, x, y);
        Bitmap output_bitmap = Bitmap.createBitmap(x, y, Config.ARGB_8888);
        np_drawable.draw(new Canvas(output_bitmap));
        return output_bitmap;
    }

    public static float getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((double) (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }

    public static RectF getRectFByPoints(float[] points) {
        return new RectF(points[0], points[1], points[2], points[5]);
    }

    public static Rect getRectByPoints(int[] points) {
        return new Rect(points[0], points[1], points[2], points[5]);
    }

    public static float[] getInnerPoints(float[] points, float ratio) {
        float[] inner = new float[8];
        float width = points[2] - points[0];
        float height = points[7] - points[1];
        inner[0] = points[0] + (width / ratio);
        inner[1] = points[1] + (height / ratio);
        inner[2] = points[2] - (width / ratio);
        inner[3] = points[3] + (height / ratio);
        inner[4] = points[4] - (width / ratio);
        inner[5] = points[5] - (height / ratio);
        inner[6] = points[6] + (width / ratio);
        inner[7] = points[7] - (height / ratio);
        return inner;
    }

    public static float[] copyFloatArray(float[] ori) {
        float[] tar = new float[ori.length];
        for (int i = 0; i < ori.length; i++) {
            tar[i] = ori[i];
        }
        return tar;
    }

    public static float[] getLimitedPointOnlyRotate(float[] values) {
        float centerX = values[0];
        float centerY = values[1];
        float lastX = values[2];
        float lastY = values[3];
        float targetX = values[4];
        float targetY = values[5];
        float limitedR = getDistanceBetweenPoints(centerX, centerY, lastX, lastY);
        float[] result = new float[3];
        float targetR = getDistanceBetweenPoints(centerX, centerY, targetX, targetY);
        float[] computeValues = new float[]{getDistanceBetweenPoints(centerX, centerY, targetX, targetY), limitedR, centerX, targetX};
        result[0] = getRePointValue(computeValues);
        computeValues[0] = centerY;
        computeValues[1] = targetY;
        result[1] = getRePointValue(computeValues);
        result[2] = targetR / limitedR;
        return result;
    }

    public static float[] getLimitedPoint(float[] values) {
        float centerX = values[0];
        float centerY = values[1];
        float lastX = values[2];
        float lastY = values[3];
        float targetX = values[4];
        float targetY = values[5];
        float minR = (getDistanceBetweenPoints(centerX, centerY, lastX, lastY) / values[6]) * MIN_SCALE;
        float maxR = minR * MAX_SCALE;
        float targetR = getDistanceBetweenPoints(centerX, centerY, targetX, targetY);
        float[] result = new float[2];
        if (targetR < minR || targetR > maxR) {
            float[] computeValues = new float[4];
            computeValues[2] = targetR;
            if (targetR > maxR) {
                computeValues[3] = maxR;
            } else if (targetR < minR) {
                computeValues[3] = minR;
            }
            computeValues[0] = centerX;
            computeValues[1] = targetX;
            result[0] = getRePointValue(computeValues);
            computeValues[0] = centerY;
            computeValues[1] = targetY;
            result[1] = getRePointValue(computeValues);
        } else {
            result[0] = targetX;
            result[1] = targetY;
        }
        return result;
    }

    private static float getRePointValue(float[] values) {
        float center = values[0];
        float target = values[1];
        return center - (((center - target) * values[3]) / values[2]);
    }

    public static float[] getTextRectPoints(Paint paint, String text) {
        return getTextRectPoints(paint, text, new Point(0, 0));
    }

    public static float[] getTextRectPoints(Paint paint, String text, Point point) {
        Rect textBounds = getTextRect(paint, text);
        RectF rectF = new RectF();
        rectF.bottom = (float) (point.y + (textBounds.height() / 2));
        rectF.top = rectF.bottom - ((float) textBounds.height());
        if (paint.getTextAlign() == Align.LEFT) {
            rectF.left = (float) point.x;
            rectF.right = (float) (point.x + textBounds.width());
        } else if (paint.getTextAlign() == Align.RIGHT) {
            rectF.left = (float) (point.x - textBounds.width());
            rectF.right = (float) point.x;
        } else if (paint.getTextAlign() == Align.CENTER) {
            rectF.left = (float) (point.x - (textBounds.width() / 2));
            rectF.right = (float) (point.x + (textBounds.width() / 2));
        }
        rectF.top -= (float) 15;
        rectF.bottom += (float) 15;
        rectF.left -= (float) 15;
        rectF.right += (float) 15;
        return getPointsByRectF(rectF);
    }

    public static Rect getTextRect(Paint paint, String text) {
        Vector<String> textLines = getTextLinesVector(text);
        int lines = textLines.size();
        Rect boundsRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), boundsRect);
        int hSum = boundsRect.height() * lines;
        int wMax = 0;
        Iterator it = textLines.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            Rect tb = new Rect();
            paint.getTextBounds(str, 0, str.length(), tb);
            int w = tb.width();
            if (w > wMax) {
                wMax = w;
            }
        }
        Rect textBounds = new Rect();
        textBounds.left = 0;
        textBounds.top = 0;
        textBounds.right = wMax;
        textBounds.bottom = hSum;
        return textBounds;
    }

    public static float[] getPointsByRectF(RectF rectF) {
        return new float[]{rectF.left, rectF.top, rectF.right, rectF.top, rectF.right, rectF.bottom, rectF.left, rectF.bottom};
    }

    public static Vector getTextLinesVector(String content) {
        Vector mString = new Vector();
        int istart = 0;
        int count = content.length();
        for (int i = 0; i < count; i++) {
            if (content.charAt(i) == '\n') {
                mString.addElement(content.substring(istart, i));
                istart = i + 1;
            } else if (i == count - 1) {
                mString.addElement(content.substring(istart, count));
            }
        }
        return mString;
    }

    private static float getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return fm.bottom - fm.top;
    }
}
