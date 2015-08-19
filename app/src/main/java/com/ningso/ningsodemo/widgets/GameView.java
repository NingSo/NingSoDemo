package com.ningso.ningsodemo.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by NingSo on 15/8/18.
 */
public class GameView extends View {
    private int cx;
    private int cy;
    float radius = 10;
    private Paint paint;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            changePosition(msg.arg1, msg.arg2);
        }
    };

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.cx = 20;
        this.cy = 20;
        this.paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(200, 200);
        canvas.drawCircle(0, 0, radius++, paint);

        if (radius > 100) {
            radius = 10;
        }

        invalidate();//通过调用这个方法让系统自动刷新视图
        //canvas.drawCircle(cx, cy, 50, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int motionEvent = event.getAction();
        switch (motionEvent) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                // changePosition(x, y);
                GramTheread grame = new GramTheread(x, y);
                grame.start();
                return true;
        }
        return super.onTouchEvent(event);
    }


    //method 1 in Main Thread
    private void changePosition(int x, int y) {
        cx = x;
        cy = y;
        invalidate();
    }

    private class GramTheread extends Thread {
        private int x;
        private int y;

        public GramTheread(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            super.run();
            Message msg = mHandler.obtainMessage();
            msg.arg1 = x;
            msg.arg2 = y;
            msg.sendToTarget();
        }
    }
}
