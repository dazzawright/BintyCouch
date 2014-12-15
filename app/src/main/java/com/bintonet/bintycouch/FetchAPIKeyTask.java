package com.bintonet.bintycouch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Darren on 21/08/2014.
 */
public class FetchAPIKeyTask extends AsyncTask<String, Void, String>{
    private final String LOG_TAG = FetchAPIKeyTask.class.getSimpleName();
    private final Context mContext;

    //    public FetchWantedTask(Context context, ArrayAdapter<String> wantedMovieAdapter) {
    public FetchAPIKeyTask(Context context){
        mContext = context;
    }

    private String getAPIKeyFromJson(String apiKeyJsonStr)
            throws JSONException {

        JSONObject wantedJson = new JSONObject(apiKeyJsonStr);
        String apiKey = wantedJson.getString("api_key");

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
//        prefs.edit().putString(mContext.getString(R.string.pref_api_key_key), apiKey).commit();

//        String apiKey = String.valueOf(apiKeyJsonStr.getClass());
        Log.v(LOG_TAG, " API KEY is " + wantedJson.getString("api_key"));
        return apiKey;
    }

    public void updatePrefs(final String result)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v("API KEy has BEEN RETURNED", result);
                Log.v("Package name is", mContext.getPackageName());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                prefs.edit().putString(mContext.getString(R.string.pref_api_key_key), result).apply();

            }
        }).start();
    }

    @Override
    protected String doInBackground(String... params) {

        // If there's no API Key, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String host_address = params[0];
        String server_port = params[1];
        String api_call = "getkey/?p=md5(password)&u=md5(username)";
        String apiKeyJsonStr = null;

        try {
            // Construct the URL for the CouchPotato Call

            URL url = new URL(
                    "http://"+host_address+":"+server_port+"/"+api_call);

            Log.v(LOG_TAG, "Built URI " + url.toString());

//                URL url = new URL("http://bintonet.homeserver.com:5000/api/4d81f4f4eb211c427e0d5c9d51c7198b/media.list");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            Log.v(LOG_TAG, "buffer is : " + buffer);
            apiKeyJsonStr = buffer.toString();
            Log.v(LOG_TAG, "apikey json Str is : " + apiKeyJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getAPIKeyFromJson(apiKeyJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.v("On Post Execute", result);
            updatePrefs(result);


            // New data is back from the server.  Hooray!
        }
    }
}
