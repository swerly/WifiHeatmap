package com.swerly.wifiheatmap;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Seth on 7/16/2017.
 */

public class JsonFetcher extends AsyncTask<String, Void, JSONObject> {
    private JsonFetchedCallback callback;

    public JsonFetcher(JsonFetchedCallback callback){
         this.callback = callback;
     }

    @Override
    protected JSONObject doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        callback.jsonFetched(result);
    }

    public interface JsonFetchedCallback{
         void jsonFetched(JSONObject json);
    }
}
