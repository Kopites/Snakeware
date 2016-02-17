package com.example.s1300465.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class GameArea extends View {
    private int TARGET_PIXEL_SIZE = 50;
    private float PIXEL_WIDTH;
    private float PIXEL_HEIGHT;
    private GridTile grid[][];
    private int gridWidth, gridHeight;
    private final Paint paint = new Paint();

    private Direction dir;
    private int length;
    private int snakeX, snakeY;

    public GameArea(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++){
                paint.setColor(Color.TRANSPARENT);

                if(grid[x][y] == GridTile.SnakeHead){
                    paint.setColor(Color.WHITE);
                }else if(grid[x][y] == GridTile.SnakePiece){
                    paint.setColor(Color.WHITE);
                }else if(grid[x][y] == GridTile.Pickup){
                    paint.setColor(Color.GREEN);
                }

                float pixelLeft = (PIXEL_WIDTH * x);
                float pixelTop = (PIXEL_HEIGHT * y);
                canvas.drawRect(pixelLeft, pixelTop, pixelLeft + PIXEL_WIDTH, pixelTop + PIXEL_HEIGHT, paint);
            }
        }
    }

    protected void spawnSnake(int x, int y, Direction dir, int length) throws IndexOutOfBoundsException{
        this.snakeX = x;
        this.snakeY = y;
        this.dir = dir;
        this.length = length;

        grid[x][y] = GridTile.SnakeHead;

        for(int i = 1; i <= length; i++){
            switch(dir){
                case UP:
                    grid[x][y + i] = GridTile.SnakePiece;
                    break;
                case LEFT:
                    grid[x + i][y] = GridTile.SnakePiece;
                    break;
                case RIGHT:
                    grid[x - i][y] = GridTile.SnakePiece;
                    break;
                case DOWN:
                    grid[x][y - i] = GridTile.SnakePiece;
                    break;
            }
        }
    }

    protected void clearSnake(){
        grid = new GridTile[gridWidth][gridHeight];
    }

    protected int getGridWidth(){
        return gridWidth;
    }
    protected int getGridHeight(){
        return gridHeight;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
        Log.d("Size", "Size just changed to w" + width + ", h" + height);

        calculateGridSize(width, height);
    }

    protected void calculateGridSize(int width, int height){
        gridWidth = (int) Math.floor(width/TARGET_PIXEL_SIZE);
        gridHeight = (int) Math.floor(height/TARGET_PIXEL_SIZE);

        //Calculate how much extra white space didn't fit into the 50x50 pixel grid
        float xRemainder = width - (gridWidth*TARGET_PIXEL_SIZE);
        float yRemainder = height - (gridHeight*TARGET_PIXEL_SIZE);
        //Then divide it up and increase the pixel sizes to fit the screen perfectly
        PIXEL_WIDTH = TARGET_PIXEL_SIZE + xRemainder/gridWidth;
        PIXEL_HEIGHT = TARGET_PIXEL_SIZE +  yRemainder/gridHeight;

        grid = new GridTile[gridWidth][gridHeight];
    }

    protected void redraw(){
        updateGameState();
        invalidate();
    }

    protected void updateGameState() throws IndexOutOfBoundsException{
        if(dir == null){
            return;
        }
        switch(dir){
            case UP:
                snakeY = snakeY - 1;
                break;
            case LEFT:
                snakeX = snakeX - 1;
                break;
            case RIGHT:
                snakeX = snakeX + 1;
                break;
            case DOWN:
                snakeY = snakeY + 1;
                break;
        }

        clearSnake();
        spawnSnake(snakeX, snakeY, dir, length);
    }
}

enum GridTile{
    SnakeHead,
    SnakePiece,
    Pickup
}
