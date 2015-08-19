package com.ningso.ningsodemo.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by NingSo on 15/8/18.
 */
public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    LoopThread mLoopThread;

    public TestSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);///设置Surface生命周期回调
        mLoopThread = new LoopThread(mSurfaceHolder, getContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //是当SurfaceView被显示时会调用的方法，所以你需要再这边开启绘制的线程
        mLoopThread.isRunning = true;
        if (!mLoopThread.isAlive())
            mLoopThread.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //surfaceDestroyed方法是当SurfaceView被隐藏会销毁时调用的方法，在这里你可以关闭绘制的线程。
        mLoopThread.isRunning = false;
        try {
            mLoopThread.join();//保证run方法能正常执行完毕
        } catch (Exception e) {

        }
    }

    class LoopThread extends Thread {
        SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;
        float radius = 10f;
        Paint paint;

        public LoopThread(SurfaceHolder surfaceHolder, Context context) {
            this.surfaceHolder = surfaceHolder;
            this.context = context;
            isRunning = false;
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void run() {
            Canvas canvas = null;
            while (isRunning) {
                try {
                    synchronized (surfaceHolder) {
                        canvas = surfaceHolder.lockCanvas(null);
                        drawView(canvas);
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void drawView(Canvas canvas) {
            canvas.drawColor(Color.WHITE);//非常重要，清除掉上次绘制的图像，非常重要！非常重要！非常重要，说三遍
            canvas.translate(250, 250);
            canvas.drawCircle(100, 100, radius++, paint);
            if (radius > 100) {
                radius = 10;
            }
        }
    }
}
