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
    private float PIXEL_WIDTH = 50;
    private float PIXEL_HEIGHT = 50;
    private GridTile grid[][];
    private int gridWidth, gridHeight;
    private final Paint paint = new Paint();

    public GameArea(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        for(int i = 0; i < gridWidth; i++){
            for(int j = 0; j < gridHeight; j++){
                Random rnd = new Random();
                paint.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                float pixelLeft = PIXEL_WIDTH * i;
                float pixelTop = PIXEL_HEIGHT * j;
                canvas.drawRect(pixelLeft, pixelTop, pixelLeft + PIXEL_WIDTH, pixelTop + PIXEL_HEIGHT, paint);
            }
        }
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
        Log.d("Pixel Width", PIXEL_WIDTH + "");
        Log.d("Pixel Height", PIXEL_HEIGHT + "");
        Log.d("Horizontal Pixels", gridWidth + "");
        Log.d("Vertical Pixels", gridHeight + "");

        grid = new GridTile[gridWidth][gridHeight];
    }

    protected void redraw(){
        invalidate();
    }
}

enum GridTile{
    SnakeHead,
    SnakePiece,
    Pickup
}
