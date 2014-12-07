package com.cs371m.ads.karma_farm;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class KFRelativeLayout extends RelativeLayout {

    public KFRelativeLayout (Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        // do what you need to with the event, and then...
        return super.dispatchTouchEvent(e);
    }
}

