package com.example.s1300465.snake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        NetworkInfo info = extras.getParcelable("networkInfo");
        NetworkInfo.State state = info.getState();
        Log.d("NetworkReceiver", info.toString() + " " + state.toString());

        if (state == NetworkInfo.State.CONNECTED) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://mayar.abertay.ac.uk/~1300465/snake/call.php");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.connect();

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            new RemoteDatabaseHelper(context).uploadData();
                            Log.d("Conn", "Connected to remote DB");
                        }else{
                            Log.d("Conn", "Connection to remote DB failed");
                        }

                        connection.disconnect();
                    }catch(IOException ex){
                        Log.w("Connection", "Remote DB connection timed out");
                    }
                }
            });

            thread.start();
        }
    }
}
