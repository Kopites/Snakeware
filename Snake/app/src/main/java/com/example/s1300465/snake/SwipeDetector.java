package com.example.s1300465.snake;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GameScreen game;

    public SwipeDetector(GameScreen game){
        this.game = game;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Direction flingDirection = null;

        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
            flingDirection = Direction.LEFT;
        }else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
            flingDirection = Direction.RIGHT;
        }

        if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
            flingDirection = Direction.UP;
        }else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
            flingDirection = Direction.DOWN;
        }

        if(flingDirection != null) {
            game.changeSnakeDirection(flingDirection);
        }

        return false;
    }
}
