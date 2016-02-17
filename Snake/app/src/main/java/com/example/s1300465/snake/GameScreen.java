package com.example.s1300465.snake;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameScreen extends AppCompatActivity {
    GameArea gameArea;
    boolean gameRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        gameArea = (GameArea) findViewById(R.id.gameArea);

        initGameLoop();
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameRunning = false;
    }
    @Override
    protected void onStop(){
        super.onStop();
        gameRunning = false;
    }

    protected void initGameLoop(){
        final int refreshRate = 1000;

        final Handler gameLoop = new Handler();
        final Runnable refresh = new Runnable() {
            @Override
            public void run() {
                if(gameRunning) {
                    gameArea.redraw();
                    gameLoop.postDelayed(this, refreshRate);
                }
            }
        };

        gameRunning = true;

        refresh.run();
    }
}
