package com.example.s1300465.snake;

import android.graphics.Path;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameScreen extends AppCompatActivity {
    GameArea gameArea;
    boolean gameRunning = false;
    Direction direction = Direction.UP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        gameArea = (GameArea) findViewById(R.id.gameArea);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initNewGame();
            }
        }, 1000);
    }

    protected void initNewGame(){
        initGameLoop();

        int spawnX = Math.round(gameArea.getGridWidth()/2);
        int spawnY = Math.round(gameArea.getGridHeight()/2);
        gameArea.spawnSnake(spawnX, spawnY, direction, 3);
    }

    protected void initGameLoop(){
        final int refreshRate = 500;

        final Handler gameLoop = new Handler();
        final Runnable refresh = new Runnable() {
            @Override
            public void run() {
                if(gameRunning) {
                    doGameLoop();
                    gameLoop.postDelayed(this, refreshRate);
                }
            }
        };

        gameRunning = true;

        refresh.run();
    }

    protected void doGameLoop(){
        try {
            gameArea.redraw();
        }catch(IndexOutOfBoundsException ex){
            Log.w("Died", "You died!");
            gameRunning = false;
        }
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
}

enum Direction{
    UP,
    DOWN,
    LEFT,
    RIGHT
}
