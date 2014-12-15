package com.bintonet.bintycouch;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bintonet.bintycouch.data.FoundMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity {

    private ListView FoundMoviesListView;
    private SimpleAdapter mSearchMovieAdapter;

//    private SearchMovieAdapter<String> mSearchMovieAdapter;

    private final String LOG_TAG = SearchActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MY", "search activity triggered");
        setContentView(R.layout.activity_search);
                String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        List<String> searchedMovieList = new ArrayList<String>(Arrays.asList(data));

//        mSearchMovieAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, searchedMovieList);
//        mSearchMovieAdapter = new SearchAdapter(this, null, 0);



        FoundMoviesListView = (ListView) findViewById(R.id.listview_search);
//        FoundMoviesListView.setAdapter(mSearchMovieAdapter);
        handleIntent(getIntent());
    }



    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void onListItemClick(ListView l,
                                View v, int position, long id) {
        // call detail activity for clicked entry
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);

            doSearch(query);
        }
    }

    private void doSearch(String queryStr) {

        String api_key = Utility.getAPIKey(this);
        String host_address = Utility.getHostAddress(this);
        String server_port = Utility.getServerPort(this);

        new SearchMovieTask().execute(api_key, host_address, server_port, queryStr);
    }

    private class SearchMovieTask extends AsyncTask<String, Void, List<Map>>

    {

        private final String LOG_TAG = SearchMovieTask.class.getSimpleName();

        private List<Map> getSearchMovieDataFromJson(String wantedJsonStr)
        throws JSONException {

        final String OWM__ID = "_id";
        final String OWM_MOVIES = "movies";
        final String OWM_TITLE = "original_title";
        final String OWM_INFO = "info";
        final String OWM_YEAR = "year";
        final String OWM_IMAGES = "images";
        final String OWM_POSTER_ORIGINAL = "poster_original";
        final String OWM_BACKDROP_ORIGINAL = "backdrop_original";
        final String OWM_PLOT = "plot";
        final String OWM_TAGLINE = "tagline";
        final String OWM_IMDB = "imdb";
        final String OWM_STATUS = "status";

            ArrayList<FoundMovie> movies = new ArrayList<FoundMovie>();
            ArrayList<Map> movieList = new ArrayList<Map>();
            // Hashmap for ListView
//            List<HashMap<String, String>> movieList = new List<HashMap<String, String>>();


        JSONObject wantedJson = new JSONObject(wantedJsonStr);
        JSONArray wantedMoviesArray = wantedJson.getJSONArray(OWM_MOVIES);

        String[] resultStrs = new String[wantedMoviesArray.length()];

        for(int i = 0; i < wantedMoviesArray.length(); i++) {

            String cp_id;
            String status;
            String title;
            String poster_original;
            String backdrop_original;
            String tagline;
            String plot;
            String imdb;
            String posterStr =null;
            Integer year;
            String TITLE = "title";
            String IMAGE = "image";
//            Map map = new HashMap();
            // Hashmap for ListView
//            ArrayList<HashMap<String, String>> map = new ArrayList<HashMap<String, String>>();
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();


            //Get the JSON object array representing the movie
            JSONObject movieObject = wantedMoviesArray.getJSONObject(i);
            title = movieObject.getString(OWM_TITLE);
            map.put(TITLE, title);
            year = movieObject.getInt(OWM_YEAR);
            imdb = movieObject.getString(OWM_IMDB);
            //Get the JSON object for Images
            JSONObject movieImagesObject = movieObject.getJSONObject(OWM_IMAGES);
            JSONArray posterArray = movieImagesObject.getJSONArray(OWM_POSTER_ORIGINAL);
            for(int n = 0 ; n < posterArray.length(); n++){
                posterStr = posterArray.getString(n);
             }
            try {
                map.put(IMAGE, posterStr);
                }
                catch(Exception ee)
            {
                Log.e("Wanted Adapter - loading image error", "Using default");
//                map.put("userIcon", R.drawable.ic_launcher);
            }


            Log.v(LOG_TAG, "Movie Images Poster String: " + posterStr);
            Log.v(LOG_TAG, "Adding Movie Title: " + title);

//            FoundMovie movie = new FoundMovie();
////            movie.poster = posterStr;
//            movie.title = title;
//            movie.imdb = imdb;
//
//            movies.add(movie);

            resultStrs[i] = title + " - " + year + " - " + imdb;

//            List<Map> list = new ArrayList<Map>();

//            Map map = new HashMap();


//            map.put(YEAR, year);
            movieList.add(map);
//            System.out.println(map);



        }
//            System.out.println(movieList);
//        return resultStrs;
          return movieList;
//            return list;
    }


        @Override
        protected ArrayList<Map> doInBackground(String... params) {


        // If there's no API Key, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String searchJsonStr = null;
        String api_key = params[0];
        String host_address = params[1];
        String server_port = params[2];
        String api_call = "movie.search";
        String search_param = params[3].replaceAll(" ", "%20");

        try {
            // Construct the URL for the CouchPotato Call

            URL url = new URL(
                    "http://"+host_address+":"+server_port+"/api/"+api_key+"/"+api_call+"?q="+search_param);

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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            searchJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Search Movies string: " + searchJsonStr);
        } catch (IOException e) {
            Log.e("SearchActivity", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("SearchActivity", "Error closing stream", e);
                }
            }
        }
        try {
            return (ArrayList<Map>) getSearchMovieDataFromJson(searchJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
        }
        return null;
    }

        protected void onPostExecute(List result) {
        if (result != null) {
            System.out.println("In Post Execute");
            System.out.println(result);
//            mSearchMovieAdapter = new SimpleAdapter(SearchActivity.this, result,
//                    R.layout.list_item_search, new String[] { "userIcon", "title", "year" },
//                    new int[] { R.id.list_item_icon, R.id.list_item_title_textview, R.id.list_item_year_textview });
            SearchAdapter mSearchMovieAdapter =  new SearchAdapter(
                    SearchActivity.this, result,
                    R.layout.list_item_search, new String[] {},
                    new int[] {});
            FoundMoviesListView.setAdapter(mSearchMovieAdapter);
        }
    }



    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            InputStream in = new java.net.URL(src).openStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 120, 160, false);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                final int THUMBNAIL_SIZE = 80;
                mIcon11 = Bitmap.createScaledBitmap(mIcon11, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
//                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}
