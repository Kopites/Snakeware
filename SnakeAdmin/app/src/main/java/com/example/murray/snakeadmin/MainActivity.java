package com.example.murray.snakeadmin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements APIResponse{
    ArrayList<String> phones = new ArrayList<>();
    ListView listView;
    PhoneListAdapter phoneListAdapter;
    SwipeRefreshLayout refresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDevices();

        listView = (ListView) findViewById(R.id.phonesList);
        phoneListAdapter = new PhoneListAdapter(this, phones);
        listView.setAdapter(phoneListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DeviceData.class);
                intent.putExtra("deviceID", parent.getAdapter().getItem(position).toString());
                startActivity(intent);
            }
        });

        refresher = (SwipeRefreshLayout) findViewById(R.id.refreshDeviceList);

        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDevices();
                refresher.setRefreshing(false);
            }
        });
    }

    public void loadDevices(){
        phones.clear();
        if(phoneListAdapter != null) {
            phoneListAdapter.notifyDataSetChanged();
        }
        (findViewById(R.id.prgLoadingPhones)).setVisibility(View.VISIBLE);
        new API(this).fetchPhones();
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

                (findViewById(R.id.prgLoadingPhones)).setVisibility(View.INVISIBLE);
                phoneListAdapter.notifyDataSetChanged();
            }catch(JSONException ex) {
                Log.w("JSON", "Something went wrong with server data");
            }
        }else{
            Toast.makeText(getApplicationContext(), "Could not connect to Mayar!", Toast.LENGTH_SHORT).show();
            (findViewById(R.id.prgLoadingPhones)).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
    }
}
