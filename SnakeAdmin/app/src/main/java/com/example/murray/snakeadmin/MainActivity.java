package com.example.murray.snakeadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements APIResponse{
    ArrayList<String> phones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new API(this).fetchPhones();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            }catch(JSONException ex) {
                Log.d("JSON", "Something went wrong with server data");
            }
        }
    }
}
