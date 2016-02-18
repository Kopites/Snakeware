package com.example.s1300465.snake;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.graphics.Path;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class GameScreen extends Activity {
    GameArea gameArea;
    TextView txtScore;
    boolean gameRunning = false;
    Direction direction = Direction.UP;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        gameArea = (GameArea) findViewById(R.id.gameArea);
        txtScore = (TextView) findViewById(R.id.txtScore);

        updateScoreLabel();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initNewGame();
            }
        }, 1000);
    }

    protected void initNewGame(){
        initGameLoop();

        updateScoreLabel();

        final GestureDetector gestureDetector = new GestureDetector(this, new SwipeDetector(this));

        gameArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        int spawnX = Math.round(gameArea.getGridWidth()/2);
        int spawnY = Math.round(gameArea.getGridHeight()/2);
        gameArea.spawnSnake(spawnX, spawnY, direction, 3);
    }

    protected void initGameLoop(){
        final int refreshRate = 100;

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

            if(new Random().nextInt(10) == 0 && gameArea.numFruit() == 0){
                gameArea.spawnFruit();
            }

            if(gameArea.ateFruit()){
                score++;
                gameArea.increaseSnakeLength(1);
                updateScoreLabel();
            }

            if(gameArea.isDead()){
                playerDied();
            }

        }catch(IndexOutOfBoundsException ex){
            playerDied();
        }
    }

    protected void playerDied(){
        Log.w("Died", "You died!");
        gameRunning = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.save_your_score)
                .setTitle(R.string.game_over);

        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_gameover, null));

        final Activity activity = this;
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO: Save the users score to a local database
                activity.finish();
            }
        });
        builder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
            }
        });

        final AlertDialog dialog = builder.create();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        }, 1000);
    }

    protected void updateScoreLabel(){
        Resources res = getResources();
        txtScore.setText(res.getString(R.string.score, score));
    }

    protected void changeSnakeDirection(Direction dir){
        gameArea.changeSnakeDirection(dir);
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
    RIGHT;

    private Direction opposite;

    static {
        UP.opposite = DOWN;
        DOWN.opposite = UP;
        LEFT.opposite = RIGHT;
        RIGHT.opposite = LEFT;
    }

    public Direction getOppositeDirection() {
        return opposite;
    }
}
