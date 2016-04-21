package com.example.murray.snakeadmin;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeviceData extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private String deviceID;
    private SwipeRefreshLayout refresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data);
        Intent intent = getIntent();
        deviceID = intent.getStringExtra("deviceID");

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setDeviceID(deviceID);
        mTitle = getString(R.string.title_phonecalls);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        refresher = (SwipeRefreshLayout) findViewById(R.id.refreshEventList);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //On refresh,
                // Get the fragment being shown, then re-load it
                FragmentManager fragmentManager = getSupportFragmentManager();
                PlaceholderFragment fragment = (PlaceholderFragment) fragmentManager.findFragmentById(R.id.container);
                fragment.loadEvents();
                refresher.setRefreshing(false);
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Intent intent = getIntent();
        deviceID = intent.getStringExtra("deviceID");
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1, deviceID))
                .commit();

        switch (position + 1) {
            case 1:
                mTitle = getString(R.string.title_phonecalls);
                break;
            case 2:
                mTitle = getString(R.string.title_received_sms);
                break;
            case 3:
                mTitle = getString(R.string.title_sent_sms);
                break;
        }
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onNavigationDrawerClosed() {
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onNavigationDrawerOpened() {
        getSupportActionBar().setTitle("Device " + deviceID);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_data, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.choose_another_device){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements APIResponse {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static String deviceID;
        private ListView listView;
        private static ArrayList<JSONObject> list;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String id) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            deviceID = id;
            list = new ArrayList<>();
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_device_data, container, false);
            listView = (ListView) rootView.findViewById(R.id.lstDataView);

            loadEvents();

            listView.setEmptyView(rootView.findViewById(R.id.txtEmptyList));

            return rootView;
        }

        public void loadEvents(){
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            if(section == 1) {
                new API(this).fetchPhoneCalls(deviceID);
                CallListAdapter callListAdapter = new CallListAdapter(getActivity(), list);
                listView.setAdapter(callListAdapter);
            }else if(section == 2){
                new API(this).fetchReceivedSMS(deviceID);
                SMSAdapter smsAdapter = new SMSAdapter(getActivity(), list, false);
                listView.setAdapter(smsAdapter);
            }else if(section == 3){
                new API(this).fetchSentSMS(deviceID);
                SMSAdapter smsAdapter = new SMSAdapter(getActivity(), list, true);
                listView.setAdapter(smsAdapter);
            }
        }

        @Override
        public void resultsReturned(JSONObject results) {
            //When the API returns the data we want
            //Clear the list that's being displayed, then refill it
            //with JSONObjects of each item
            if(results != null) {
                list.clear();
                try {
                    //Convert the returned JSON list of deviceIDs into an ArrayList
                    Iterator<?> keys = results.keys();
                    while(keys.hasNext()) {
                        String key = (String) keys.next();

                        if(results.get(key) instanceof JSONObject){
                            list.add(((JSONObject) results.get(key)));
                        }
                    }

                    //Then get the current adapter and tell it to update
                    ((ArrayAdapter<JSONObject>) listView.getAdapter()).notifyDataSetChanged();
                }catch(JSONException ex) {
                    Log.w("JSON", "Something went wrong with server data");
                }
            }else{
                Toast.makeText(getContext(), "Could not connect to Mayar!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
