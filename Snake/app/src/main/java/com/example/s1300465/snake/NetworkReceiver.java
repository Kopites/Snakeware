package com.example.s1300465.snake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //When the Network connection changes, check if we're connected
        Bundle extras = intent.getExtras();
        NetworkInfo info = extras.getParcelable("networkInfo");
        NetworkInfo.State state = info.getState();
        Log.d("NetworkReceiver", info.toString() + " " + state.toString());

        //If so, upload
        if (state == NetworkInfo.State.CONNECTED) {
            new RemoteDatabaseHelper(context).checkConnectionAndUpload();
        }
    }
}
