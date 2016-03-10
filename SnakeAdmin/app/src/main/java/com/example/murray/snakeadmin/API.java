package com.example.murray.snakeadmin;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

public class API {
    APIResponse obj;
    final String URL = "http://mayar.abertay.ac.uk/~1300465/snake";

    public API(APIResponse obj){
        this.obj = obj;
    }

    public void fetchPhones(){
        new APIJSONFetcher(obj).execute(URL + "/get_phones.php");
    }
    public void fetchPhoneCalls(String deviceID){
        new APIJSONFetcher(obj).execute(URL + "/get_calls.php?deviceID=" + deviceID);
    }
    public void fetchSentSMS(String deviceID) {
        new APIJSONFetcher(obj).execute(URL + "/get_sms.php?outgoing=true&deviceID=" + deviceID);
    }
    public void fetchReceivedSMS(String deviceID){
        new APIJSONFetcher(obj).execute(URL + "/get_sms.php?outgoing=false&deviceID=" + deviceID);
    }
}

class APIJSONFetcher extends AsyncTask<String, String, JSONObject> {
    APIResponse obj;
    int status;

    public APIJSONFetcher(APIResponse obj){
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
        }catch(UnknownHostException ex){
            Log.w("API", "Couldn't find server - no internet connection?");
            this.cancel(true);
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(JSONObject result){
        if(status >= HttpURLConnection.HTTP_BAD_REQUEST){
            try {
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
