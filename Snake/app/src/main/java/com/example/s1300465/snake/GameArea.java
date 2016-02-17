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
    private int PIXEL_SIZE = 50;
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
                int pixelLeft = PIXEL_SIZE * i;
                int pixelTop = PIXEL_SIZE * j;
                canvas.drawRect(pixelLeft, pixelTop, pixelLeft + PIXEL_SIZE, pixelTop + PIXEL_SIZE, paint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
        Log.d("Size", "Size just changed to w" + width + ", h" + height);

        gridWidth = (int) Math.floor(width/PIXEL_SIZE);
        gridHeight = (int) Math.floor(height/PIXEL_SIZE);

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
