package com.example.murray.snakeadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements APIResponse{
    ArrayList<String> phones = new ArrayList<>();
    ListView listView;
    PhoneListAdapter phoneListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new API(this).fetchPhones();

        listView = (ListView) findViewById(R.id.phonesList);
        phoneListAdapter = new PhoneListAdapter(this, phones);
        listView.setAdapter(phoneListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Activity", "Opening snoop activity for device " + parent.getAdapter().getItem(position));
            }
        });
    }

    @Override
    public void resultsReturned(JSONObject results){
        if(results != null) {
            try {
                //Convert the returned JSON list of deviceIDs into an ArrayList
                Iterator<?> keys = results.keys();
                while(keys.hasNext()) {
                    String key = (String) keys.next();
                    if(results.get(key) instanceof JSONObject){
                        phones.add(((JSONObject) results.get(key)).getString("deviceID"));
                    }
                }

                phoneListAdapter.notifyDataSetChanged();
            }catch(JSONException ex) {
                Log.w("JSON", "Something went wrong with server data");
            }
        }
    }
}
