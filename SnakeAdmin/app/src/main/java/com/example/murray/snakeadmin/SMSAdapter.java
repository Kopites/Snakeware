package com.example.murray.snakeadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SMSAdapter extends ArrayAdapter<JSONObject> {
    private final Activity context;
    private List<JSONObject> texts;
    private boolean outgoing; //true if this adapter is displaying sent texts, else false

    public SMSAdapter(Activity context, List<JSONObject> texts, Boolean outgoing){
        super(context, R.layout.phone_list, texts);

        this.context = context;
        this.texts = texts;
        this.outgoing = outgoing;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sms_list, null, true);
        final JSONObject sms = texts.get(position);

        if(sms == null){
            return rowView;
        }

        TextView txtParticipant = (TextView) rowView.findViewById(R.id.txtParticipant);
        TextView txtMessage = (TextView) rowView.findViewById(R.id.txtMessage);
        TextView txtSentTime = (TextView) rowView.findViewById(R.id.txtSentTime);

        try {
            txtParticipant.setText((outgoing ? "To: " : "From: ") + sms.get("participant").toString());
            txtSentTime.setText(sms.get("time").toString());
            txtMessage.setText(sms.get("message").toString());
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] options = new CharSequence[]{"Edit", "Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Open event in Google Maps?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float latitude = 0, longitude = 0;

                        try {
                            latitude = Float.valueOf(((JSONObject) sms.get("location")).get("latitude").toString());
                            longitude = Float.valueOf(((JSONObject) sms.get("location")).get("longitude").toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        String uri = String.format("geo:%f,%f?q=%f,%f", latitude, longitude, latitude, longitude);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getContext().startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        return rowView;
    }
}
