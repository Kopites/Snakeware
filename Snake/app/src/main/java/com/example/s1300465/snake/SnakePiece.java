package com.example.s1300465.snake;

public class SnakePiece extends GridPiece{
    //Special grid tile that designates a piece of the Snake
    private Direction direction;

    public SnakePiece(GridTile type, Direction direction){
        super(type);
        this.direction = direction;
    }

    public void setDirection(Direction dir){
        this.direction = dir;
    }

    public Direction getDirection(){
        return direction;
    }
}

