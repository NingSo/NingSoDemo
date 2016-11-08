package com.ningso.ningsodemo.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ningso.ningsodemo.R;


public class LinkCountDownView extends View {
    private ObjectAnimator anim;
    private int value;
    private float scale = 1;
    private boolean floor;
    private Paint paint;

    private int textColor;
    private int textOutlineColor;

    private int textSize;
    private int textOutlineWidth;

    public LinkCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            value = 3;
        } else {
            textColor = getResources().getColor(R.color.link_countdown_text);
            textOutlineColor = getResources().getColor(R.color.link_countdown_outline);
            textSize = getResources().getDimensionPixelSize(R.dimen.link_countdown_text_size);
            textOutlineWidth = getResources().getDimensionPixelSize(R.dimen.link_countdown_text_outline_width);
        }
    }

    public void start(int begin, int end, long millis, Animator.AnimatorListener listener) {
        stop();

        floor = end > begin;
        anim = ObjectAnimator.ofFloat(this, "progress", begin, end);
        anim.setDuration(millis);
        anim.setInterpolator(new LinearInterpolator());
        if (listener != null) {
            anim.addListener(listener);
        }
        anim.start();

        invalidate();
    }

    public void stop() {
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
    }

    @Keep
    public void setProgress(float p) {
        if (floor) {
            value = (int) Math.floor(p);
            scale = 1 - (p - value);
        } else {
            value = (int) Math.ceil(p);
            scale = 1 - (value - p);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // only draw text while anim running
        if (anim == null || !anim.isRunning() || getVisibility() != View.VISIBLE) {
            return;
        }

        // init paint
        if (paint == null) {
            paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStrokeWidth(textOutlineWidth);
            paint.setAntiAlias(true);

            Typeface font = Typeface.create("Debussy", Typeface.BOLD);
            paint.setTypeface(font == null ? Typeface.DEFAULT_BOLD : font);
        }

        // scale text
        paint.setTextSize(textSize * (scale * .4f + .6f));

        // text info
        String text = Integer.toString(value);
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2 - (int) ((paint.ascent() + paint.descent()) * .5f);

        // text
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        canvas.drawText(text, x, y, paint);

        // outline
        paint.setColor(textOutlineColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawText(text, x, y, paint);

        // invalidate
        invalidate();
    }
}