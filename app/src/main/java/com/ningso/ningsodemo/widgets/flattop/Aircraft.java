package com.ningso.ningsodemo.widgets.flattop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

public abstract class Aircraft extends View {
    abstract void assemble(Canvas canvas);

    abstract Matrix getAirway();

    abstract boolean isFlying();

    abstract void land();

    abstract void reload(Object obj);

    abstract boolean takeOff(MotionEvent motionEvent);

    public Aircraft(Context context) {
        super(context);
    }
}
