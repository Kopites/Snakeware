package com.example.s1300465.snake;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class IncomingCallInterceptor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbh = new DatabaseHelper(context);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        //If the phone is ringing, consider that the start of a call
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            long startTime = System.currentTimeMillis();
            SharedPreferences.Editor prefsEditor = context.getSharedPreferences("com.example.s1300465.snake", Context.MODE_PRIVATE).edit();
            prefsEditor.putLong("callStartTime", startTime);
            prefsEditor.putString("incomingNumber", incomingNumber);
            prefsEditor.apply();
        }else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            //If it changes to Offhook, the call has been answered
            SharedPreferences.Editor prefsEditor = context.getSharedPreferences("com.example.s1300465.snake", Context.MODE_PRIVATE).edit();
            prefsEditor.putBoolean("answered", true);
            prefsEditor.apply();
        }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            //When it changes to Idle, the call is over
            long startTime;
            String incomingNumber;
            boolean answered;

            SharedPreferences prefs = context.getSharedPreferences("com.example.s1300465.snake", Context.MODE_PRIVATE);
            startTime = prefs.getLong("callStartTime", 0);
            incomingNumber = prefs.getString("incomingNumber", null);
            answered = prefs.getBoolean("answered", false);

            if(incomingNumber == null || startTime == 0){
                //If the phone changed to idle but there's no startTime stored, then it wasn't previously ringing
                //(probably an outgoing call)
                return;
            }

            long endTime = System.currentTimeMillis();
            long callDuration = endTime - startTime;
            if(!answered){
                //If the call was never answered, set the duration to 0
                callDuration = 0;
                //An incoming call with duration 0 will be treated as missed
            }

            dbh.savePhoneCall(incomingNumber, false, callDuration, startTime);

            prefs.edit().remove("callStartTime").remove("incomingNumber").remove("answered").apply();
        }
    }
}
