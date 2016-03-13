package com.example.s1300465.snake;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallInterceptor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbh = new DatabaseHelper(context);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        Log.d("State Change", state);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            long startTime = System.currentTimeMillis();
            Log.d("Call", "Call started (ringing)");
            Log.d("Start Time", startTime + "");
            Log.d("Incoming Number", incomingNumber + "");
            SharedPreferences.Editor prefsEditor = context.getSharedPreferences("com.example.s1300465.snake", Context.MODE_PRIVATE).edit();
            prefsEditor.putLong("callStartTime", startTime);
            prefsEditor.putString("incomingNumber", incomingNumber);
            prefsEditor.apply();
        }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            long startTime;
            String incomingNumber;

            SharedPreferences prefs = context.getSharedPreferences("com.example.s1300465.snake", Context.MODE_PRIVATE);
            startTime = prefs.getLong("callStartTime", 0);
            incomingNumber = prefs.getString("incomingNumber", null);
            
            Log.d("Call", "Call ended (possibly)");
            Log.d("Start Time", startTime + "");
            Log.d("Incoming Number", incomingNumber + "");
            if(incomingNumber == null || startTime == 0){
                //If the phone changed to idle but there's no startTime stored, then it wasn't previously ringing
                //(probably an outgoing call)
                Log.d("Call", "Not logged, incomingNumber null or startTime 0");
                return;
            }

            long endTime = System.currentTimeMillis();
            long callDuration = endTime - startTime;
            Log.d("End Time", endTime + "");
            Log.d("Duration", callDuration + "");

            dbh.savePhoneCall(incomingNumber, false, callDuration, startTime);

            prefs.edit().remove("callStartTime").remove("incomingNumber").apply();
        }
    }
}
