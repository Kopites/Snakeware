package com.example.s1300465.snake;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class IncomingCallInterceptor extends BroadcastReceiver {
    private long startTime, endTime;
    private String incomingNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbh = new DatabaseHelper(context);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            startTime = System.currentTimeMillis();
        }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            endTime = System.currentTimeMillis();
            long callDuration = endTime - startTime;

            dbh.savePhoneCall(incomingNumber, false, callDuration, startTime, 0, 0); //TODO: fetch location of call
        }
    }
}
