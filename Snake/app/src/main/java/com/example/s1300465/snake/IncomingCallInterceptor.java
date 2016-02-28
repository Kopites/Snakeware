package com.example.s1300465.snake;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallInterceptor extends BroadcastReceiver {
    private long startTime;
    private String incomingNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast", "incoming call triggered");
        DatabaseHelper dbh = new DatabaseHelper(context);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d("State", state);

        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            startTime = System.currentTimeMillis();
        }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            //If the phone changed to idle but there's no startTime stored, then it wasn't previously ringing
            //(probably an outgoing call)
            if(incomingNumber == null || startTime == 0){
                return;
            }

            long endTime = System.currentTimeMillis();
            long callDuration = endTime - startTime;

            dbh.savePhoneCall(incomingNumber, false, callDuration, startTime, 0, 0); //TODO: fetch location of call

            startTime = 0;
            incomingNumber = null;
        }
    }
}
