package com.example.s1300465.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameArea extends View {
    private int TARGET_PIXEL_SIZE = 30;
    private float PIXEL_WIDTH;
    private float PIXEL_HEIGHT;
    private GridPiece grid[][];
    private int gridWidth, gridHeight;
    private final Paint paint = new Paint();

    private Direction dir;
    private int length;
    private int snakeX, snakeY;
    private boolean ateFruit = false;

    private ArrayList<SnakePiece> snakePieces = new ArrayList<>();

    public GameArea(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++){
                paint.setColor(Color.TRANSPARENT);

                if(grid[x][y] == null){
                    continue;
                }

                if(grid[x][y].getType() == GridTile.SnakeHead){
                    paint.setColor(Color.WHITE);
                }else if(grid[x][y].getType() == GridTile.SnakePiece){
                    paint.setColor(Color.WHITE);
                }else if(grid[x][y].getType() == GridTile.Pickup){
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

        grid = new GridPiece[gridWidth][gridHeight];

        SnakePiece piece = new SnakePiece(GridTile.SnakeHead, Direction.LEFT);
        grid[x][y] = piece;
        snakePieces.add(piece);

        for(int i = 1; i <= length; i++){
            SnakePiece newPiece = new SnakePiece(GridTile.SnakePiece, dir);
            switch(dir){
                case UP:
                    grid[x][y + i] = newPiece;
                    break;
                case LEFT:
                    grid[x + i][y] = newPiece;
                    break;
                case RIGHT:
                    grid[x - i][y] = newPiece;
                    break;
                case DOWN:
                    grid[x][y - i] = newPiece;
                    break;
            }

            snakePieces.add(newPiece);
        }
    }

    protected void moveSnake(){
        Direction dirOfLastPiece = null;
        GridPiece[][] tempGrid = new GridPiece[gridWidth][gridHeight];
        tempGrid = copyFruitsToNewGrid(tempGrid);

        for(SnakePiece piece : snakePieces){
            int[] coords = findPieceLocation(piece);
            int x = coords[0];
            int y = coords[1];

            if(piece.getType() == GridTile.SnakeHead){
                dirOfLastPiece = piece.getDirection();
                GridPiece nextPiece = getPieceAhead(x, y);
                if(nextPiece != null) {
                    if(nextPiece.getType() == GridTile.Pickup){
                        ateFruit = true;
                    }
                }
                tempGrid = moveSnakePiece(x, y, tempGrid);
            }

            if(piece.getType() == GridTile.SnakePiece){
                tempGrid = moveSnakePiece(x, y, tempGrid);

                Direction temp = ((SnakePiece) grid[x][y]).getDirection();
                piece.setDirection(dirOfLastPiece);
                dirOfLastPiece = temp;
            }

        }

        grid = tempGrid;
    }

    protected boolean ateFruit(){
        if(ateFruit){
            ateFruit = false;
            return true;
        }

        return false;
    }

    protected void increaseSnakeLength(int amount){
        length = length + amount;

        for(int i = 0; i < amount; i++){
            SnakePiece tail = snakePieces.get(snakePieces.size() - 1);
            SnakePiece newPiece = new SnakePiece(GridTile.SnakePiece, tail.getDirection());
            int tailCoords[] = findPieceLocation(tail);
            int newX = tailCoords[0];
            int newY = tailCoords[1];

            switch(tail.getDirection()){
                case UP:
                    newY++;
                    break;
                case LEFT:
                    newX++;
                    break;
                case RIGHT:
                    newX--;
                    break;
                case DOWN:
                    newY--;
                    break;
            }

            grid[newX][newY] = newPiece;
            snakePieces.add(newPiece);
        }
    }

    protected GridPiece[][] copyFruitsToNewGrid(GridPiece[][] mGrid){
        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++){
                if(grid[x][y] == null){
                    continue;
                }

                if(grid[x][y].getType() == GridTile.Pickup){
                    mGrid[x][y] = grid[x][y];
                }
            }
        }
        return mGrid;
    }

    protected GridPiece getPieceAhead(int x, int y){
        GridPiece piece = null;
        SnakePiece head = (SnakePiece) grid[x][y];

        switch(head.getDirection()) {
            case UP:
                piece = grid[x][y - 1];
                break;
            case LEFT:
                piece = grid[x - 1][y];
                break;
            case RIGHT:
                piece = grid[x + 1][y];
                break;
            case DOWN:
                piece = grid[x][y + 1];
                break;
        }

        return piece;
    }

    protected GridPiece[][] moveSnakePiece(int x, int y, GridPiece[][] mGrid){
        SnakePiece piece = (SnakePiece) grid[x][y];
        GridPiece[][] tempGrid = mGrid;

        switch(piece.getDirection()){
            case UP:
                tempGrid[x][y - 1] = piece;
                break;
            case LEFT:
                tempGrid[x - 1][y] = piece;
                break;
            case RIGHT:
                tempGrid[x + 1][y] = piece;
                break;
            case DOWN:
                tempGrid[x][y + 1] = piece;
                break;
        }

        return tempGrid;
    }

    protected int[] findPieceLocation(SnakePiece piece){
        int[] coords = new int[2];
        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++){
                if(grid[x][y] == null){
                    continue;
                }

                if(grid[x][y] == piece){
                    coords[0] = x;
                    coords[1] = y;
                    break;
                }
            }
        }

        return coords;
    }

    protected void changeSnakeDirection(Direction dir){
        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++){
                if(grid[x][y] == null){
                    continue;
                }

                if(grid[x][y].getType() == GridTile.SnakeHead){
                    SnakePiece head = (SnakePiece) grid[x][y];
                    if(head.getDirection() != dir && dir != head.getDirection().getOppositeDirection()) {
                        head.setDirection(dir);
                    }
                    break;
                }
            }
        }
    }

    protected void spawnFruit(){
        Random random = new Random();
        int fruitX;
        int fruitY;

        do{
            fruitX = random.nextInt(gridWidth);
            fruitY = random.nextInt(gridHeight);
        }while(grid[fruitX][fruitY] != null);

        grid[fruitX][fruitY] = new GridPiece(GridTile.Pickup);
    }

    protected int numFruit(){
        int count = 0;

        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++){
                if(grid[x][y] == null){
                    continue;
                }

                if(grid[x][y].getType() == GridTile.Pickup){
                    count++;
                }
            }
        }

        return count;
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

        grid = new GridPiece[gridWidth][gridHeight];
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

        moveSnake();
    }
}


enum GridTile{
    SnakeHead,
    SnakePiece,
    Pickup
}
