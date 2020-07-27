package com.example.outfit.helpers;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.parse.ParseUser;

public abstract class OnTapListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    public OnTapListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            OnTapListener.this.onDoubleTap(e);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            OnTapListener.this.onSingleTapConfirmed(e);
            return super.onSingleTapConfirmed(e);
        }
    }

    public void onDoubleTap(MotionEvent e) {
        // To be overridden when implementing listener
    }

    public void onSingleTapConfirmed(MotionEvent e) {
        // To be overridden when implementing listener
    }
}
