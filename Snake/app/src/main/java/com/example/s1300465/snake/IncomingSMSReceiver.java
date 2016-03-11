package com.example.s1300465.snake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("text", "incoming sms detected");
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage sms = msgs[0];
            String message = sms.getMessageBody();
            String sender = sms.getOriginatingAddress();

            DatabaseHelper dbh = new DatabaseHelper(context);
            dbh.saveSMS(sender, false, message, System.currentTimeMillis());
        }
    }
}

