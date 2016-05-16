package com.ningso.ningsodemo.widgets.flattop;

import android.graphics.Matrix;
import android.graphics.Rect;

public interface IFlattop {
    Rect getDeckRect();

    Matrix getDeckTransformer();

    void removeAircraft(Aircraft aircraft);

    void requestInput(Object obj);
}
