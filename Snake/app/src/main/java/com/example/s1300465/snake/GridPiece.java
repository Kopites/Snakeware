package com.example.s1300465.snake;

public class GridPiece {
    private GridTile type;

    public GridPiece(GridTile type){
        this.type = type;
    }

    public GridTile getType(){
        return type;
    }
}
