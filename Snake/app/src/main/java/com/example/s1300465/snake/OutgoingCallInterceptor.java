package com.example.s1300465.snake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class OutgoingCallInterceptor extends BroadcastReceiver {
    String outgoingNumber;
    long startTime, endTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        final DatabaseHelper dbh = new DatabaseHelper(context);

        //Have to listen differently for Outgoing calls:
        // doesn't use the TelephonyManager.EXTRA_STATE, so register a listener
        // that detects when the state is changing, and when the call starts and ends
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String number) {
                //If it's gone to OFFHOOK, the call is in progress
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    startTime = System.currentTimeMillis();
                    outgoingNumber = number;
                }
                //If it's gone IDLE, the call has ended
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (outgoingNumber == null || outgoingNumber.equals("") || startTime == 0) {
                        return;
                    }
                    endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    dbh.savePhoneCall(outgoingNumber, true, duration, startTime);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
