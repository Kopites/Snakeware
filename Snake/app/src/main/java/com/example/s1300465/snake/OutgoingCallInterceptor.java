package com.example.s1300465.snake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class OutgoingCallInterceptor extends BroadcastReceiver {
    String outgoingNumber;
    long startTime, endTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbh = new DatabaseHelper(context);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        Toast.makeText(context, intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER), Toast.LENGTH_SHORT).show();
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            outgoingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            startTime = System.currentTimeMillis();
        }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            endTime = System.currentTimeMillis();
            long callDuration = endTime - startTime;

            dbh.savePhoneCall(outgoingNumber, true, callDuration, startTime, 0, 0); //TODO: fetch location of call
        }
    }
}
