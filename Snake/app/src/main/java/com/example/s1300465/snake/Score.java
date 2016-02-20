package com.example.s1300465.snake;

public class Score {
    private String name;
    private int score;

    public Score(String name, int score){
        this.name = name;
        this.score = score;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }
}
