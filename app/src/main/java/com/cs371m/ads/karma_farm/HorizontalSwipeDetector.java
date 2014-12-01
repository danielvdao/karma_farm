package com.cs371m.ads.karma_farm;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HorizontalSwipeDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }


    private static final String TAG = "HorizontalSwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    private SwipeVoteable mCallingFragment;

    public HorizontalSwipeDetector(SwipeVoteable callingFragment) {
        mCallingFragment = callingFragment;
    }

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();

                float deltaX = downX - upX;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        Log.d(TAG, "Swipe Left to Right");
                        mSwipeDetected = Action.LR;
                        mCallingFragment.onDownVote();
                        return true;
                    }
                    if (deltaX > 0) {
                        Log.d(TAG, "Swipe Right to Left");
                        mSwipeDetected = Action.RL;
                        mCallingFragment.onUpVote();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
