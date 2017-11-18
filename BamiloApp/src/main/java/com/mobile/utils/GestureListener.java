package com.mobile.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final View view;
    private GestureCallback gestureCallback;

    public GestureListener(View view, GestureCallback gestureCallback) {
        this.gestureCallback = gestureCallback;
        this.view = view;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (gestureCallback != null) {
            gestureCallback.onClick(view);
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (gestureCallback != null) {
            gestureCallback.onDoubleTap(view);
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            if (gestureCallback != null) {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            gestureCallback.onSwipeRight(view);
                        } else {
                            gestureCallback.onSwipeLeft(view);
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            gestureCallback.onSwipeBottom(view);
                        } else {
                            gestureCallback.onSwipeTop(view);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public GestureCallback getGestureCallback() {
        return gestureCallback;
    }

    public void setGestureCallback(GestureCallback gestureCallback) {
        this.gestureCallback = gestureCallback;
    }

    public static abstract class GestureCallback {
        public void onClick(View view) {

        }

        public void onDoubleTap(View view) {

        }

        public void onSwipeRight(View view) {

        }

        public void onSwipeLeft(View view) {

        }

        public void onSwipeTop(View view) {

        }

        public void onSwipeBottom(View view) {

        }
    }
}