package com.example.murray.snakeadmin;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PhoneListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private List<String> phones;

    public PhoneListAdapter(Activity context, List<String> phones){
        super(context, R.layout.phone_list, phones);

        this.context = context;
        this.phones = phones;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.phone_list, null, true);
        TextView txtDeviceID = (TextView) rowView.findViewById(R.id.txtDeviceID);

        txtDeviceID.setText(phones.get(position));

        return rowView;
    }
}
