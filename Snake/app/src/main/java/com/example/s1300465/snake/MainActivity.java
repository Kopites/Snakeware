package com.example.s1300465.snake;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbh;
    TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(this);

        //For Debugging purposes:
        (findViewById(R.id.textView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });

        if(checkPermissions()){
            storePhoneNumber();
        } // else onRequestPermissionsResult will be called
    }

    public boolean checkPermissions(){
        ArrayList<String> permissions = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED){
            permissions.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_DENIED){
            permissions.add(android.Manifest.permission.PROCESS_OUTGOING_CALLS);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED){
            permissions.add(android.Manifest.permission.RECEIVE_SMS);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(permissions.size() == 0) {
            return true;
        }

        ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 0);
        return false;
    }

    public void storePhoneNumber(){
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simSerial = tm.getSimSerialNumber();
        String operator = tm.getNetworkOperatorName();
        String voicemail = tm.getVoiceMailNumber();
        String imei = tm.getDeviceId();

        //We save the SIM Serial number rather than phone number because
        //TelephonyManager.getLine1Number() rarely works for whatever reason
        //whereas a working phone is guaranteed to have a SIM serial number for ID purposes

        Log.d("SimSerial", simSerial);
        Log.d("Network", operator);
        Log.d("IMEI", imei);
        if(simSerial != null && simSerial.length() > 0) {
            Log.d("[Not] Saving", simSerial);
            Log.d("Loc", dbh.getLocation().toString());
            //dbh.savePhone(simSerial, operator, voicemail);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(checkPermissions()){
            storePhoneNumber();
        }
    }

    public void startNewGame(View v){
        startActivity(new Intent(this, GameScreen.class));
    }

    public void openHighScores(View v){
        startActivity(new Intent(this, HighScoresActivity.class));
    }
}
