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

public class CallListAdapter extends ArrayAdapter<JSONObject> {
    private final Activity context;
    private List<JSONObject> calls;

    public CallListAdapter(Activity context, List<JSONObject> calls){
        super(context, R.layout.phone_list, calls);

        this.context = context;
        this.calls = calls;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.call_list, null, true);
        final JSONObject call = calls.get(position);

        if(call == null){
            return rowView;
        }

        TextView txtParticipant = (TextView) rowView.findViewById(R.id.txtParticipant);
        TextView txtDuration = (TextView) rowView.findViewById(R.id.txtDuration);
        TextView txtCallTime = (TextView) rowView.findViewById(R.id.txtCallTime);
        ImageView imgPhone = (ImageView) rowView.findViewById(R.id.imgPhone);

        try {
            txtDuration.setText(millisecondsToString(Integer.valueOf(call.get("duration").toString())));
            txtCallTime.setText(call.get("time").toString());

            imgPhone.setImageResource(android.R.drawable.ic_menu_call);
            if(call.get("outgoing").toString().equals("true")){
                imgPhone.setColorFilter(Color.rgb(0, 200, 0));
                txtParticipant.setText("To: ");
            }else{
                imgPhone.setColorFilter(Color.BLUE);
                imgPhone.setRotation(90);
                txtParticipant.setText("From: ");
            }

            txtParticipant.setText(txtParticipant.getText() + call.get("participant").toString());
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
                            latitude = Float.valueOf(((JSONObject) call.get("location")).get("latitude").toString());
                            longitude = Float.valueOf(((JSONObject) call.get("location")).get("longitude").toString());
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

    public String millisecondsToString(int millis){
        String output;

        output = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
        );

        return output;
    }
}
