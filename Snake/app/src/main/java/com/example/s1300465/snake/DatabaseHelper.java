package com.example.s1300465.snake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SnakeScores";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE LocalScores (Name TEXT, Score INTEGER);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public void saveLocalScore(String name, int score){
        ContentValues row = new ContentValues();
        row.put("Name", name);
        row.put("Score", score);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("LocalScores", null, row);
        db.close();
    }

    public ArrayList<Score> getLocalScores(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query("LocalScores", new String[]{"Name", "Score"}, null, null, null, null, "Score");

        ArrayList<Score> scores = new ArrayList<>();
        for(int i = 0; i < result.getCount(); i++){
            result.moveToPosition(i);
            scores.add(new Score(result.getString(0), result.getInt(1)));
            Log.d("Score " + i, result.getString(0) + ": " + result.getString(1));
        }

        return scores;
    }
}
