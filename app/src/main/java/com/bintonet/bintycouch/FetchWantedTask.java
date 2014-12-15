package com.bintonet.bintycouch;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bintonet.bintycouch.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Darren on 11/08/2014.
 */
public class FetchWantedTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchWantedTask.class.getSimpleName();
    private final Context mContext;

//    public FetchWantedTask(Context context, ArrayAdapter<String> wantedMovieAdapter) {
    public FetchWantedTask(Context context){
        mContext = context;
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     * @return the row ID of the added movie.
     * title, year, tagline, plot, imdb, poster_original, backdrop_original
     */
    private long addMovie(String cp_id, String title, String status, Integer year, String quality, String tagline, String plot, String imdb, String runtime, String poster_original, String backdrop_original,  ArrayList<String> genreStringArray) {

        // First, check if the location with this city name exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.WantedEntry.CONTENT_URI,
                new String[]{MovieContract.WantedEntry.COLUMN_CP_ID},
                MovieContract.WantedEntry.COLUMN_CP_ID + " = ?",
                new String[]{cp_id},
                null);

        if (cursor.moveToFirst()) {

            int wantedMovieIdIndex = cursor.getColumnIndex(MovieContract.WantedEntry.COLUMN_CP_ID);
            Log.v(LOG_TAG, "Found " + title + " in the database!" );
            return cursor.getLong(wantedMovieIdIndex);
        } else {
            Log.v(LOG_TAG, "Didn't find it in the database, inserting now!");
            ContentValues wantedMovieValues = new ContentValues();
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_CP_ID, cp_id);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_TITLE, title);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_YEAR, year);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_STATUS, status);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_QUALITY, quality);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_RUNTIME, runtime);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_TAGLINE, tagline);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_PLOT, plot);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_IMDB, imdb);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_POSTER_ORIGINAL, poster_original);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_BACKDROP_ORIGINAL, backdrop_original);
            wantedMovieValues.put(MovieContract.WantedEntry.COLUMN_GENRES, Utility.convertArrayToString(genreStringArray));

            Uri wantedMovieInsertUri = mContext.getContentResolver()
                    .insert(MovieContract.WantedEntry.CONTENT_URI, wantedMovieValues);

            Bitmap mIcon11 = null;
            String thumbnailFilename;
            String fullsizeFilename;
            if(poster_original != null) {
                try {
                    InputStream in = new java.net.URL(poster_original).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    Bitmap thumbnail = Bitmap.createScaledBitmap(mIcon11, 160, 240, false);
                    thumbnailFilename = cp_id+"_thumb";
                    fullsizeFilename = cp_id+"_full";
                    String path = saveToInternalStorage(thumbnail, thumbnailFilename);
                    String fullsizePath = saveToInternalStorage(mIcon11, fullsizeFilename);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
//                e.printStackTrace();
                }
            }
            else{
                mIcon11 = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.movie_placeholder);
                Bitmap thumbnail = Bitmap.createScaledBitmap(mIcon11, 160, 240, false);
                thumbnailFilename = cp_id+"_thumb";
                fullsizeFilename = cp_id+"_full";
                String thumbPath = saveToInternalStorage(thumbnail, thumbnailFilename);
                String fullsizePath = saveToInternalStorage(mIcon11, fullsizeFilename);
            }


            return ContentUris.parseId(wantedMovieInsertUri);
        }
    }



    private String[] getWantedMovieDataFromJson(String wantedJsonStr)
            throws JSONException {

        final String OWM__ID = "_id";
        final String OWM_MOVIES = "movies";
        final String OWM_TITLE = "title";
        final String OWM_INFO = "info";
        final String OWM_YEAR = "year";
        final String OWM_IMAGES = "images";
        final String OWM_POSTER_ORIGINAL = "poster_original";
        final String OWM_BACKDROP_ORIGINAL = "backdrop_original";
        final String OWM_PLOT = "plot";
        final String OWM_TAGLINE = "tagline";
        final String OWM_IMDB = "imdb";

        // to be added below here
        final String OWM_GENRES = "genres";
        final String OWM_QUALITY = "profile_id";
        final String OWM_STATUS = "status";
        final String OWM_RUNTIME = "runtime";


        JSONObject wantedJson = new JSONObject(wantedJsonStr);
        JSONArray wantedMoviesArray = wantedJson.getJSONArray(OWM_MOVIES);

        String[] resultStrs = new String[wantedMoviesArray.length()];

        for(int i = 0; i < wantedMoviesArray.length(); i++) {

            String cp_id;
            String title;
            String backdrop_original;
            String tagline;
            String plot;
            String imdb;
            String posterStr =null;
            Integer year;
            String runtime;
            String status;
            // To be implemented below here ..
            String poster_original;
            String quality;

            //Get the JSON object array representing the movie
            JSONObject movieObject = wantedMoviesArray.getJSONObject(i);
            title = movieObject.getString(OWM_TITLE);
            cp_id = movieObject.getString(OWM__ID);
            status = movieObject.getString(OWM_STATUS);
            quality = movieObject.getString(OWM_QUALITY);

            //Get the JSON object for movie info
            JSONObject movieInfoObject = movieObject.getJSONObject(OWM_INFO);
            year = movieInfoObject.getInt(OWM_YEAR);
            tagline = movieInfoObject.getString(OWM_TAGLINE);
            plot = movieInfoObject.getString(OWM_PLOT);
            imdb = movieInfoObject.getString(OWM_IMDB);
            runtime = movieInfoObject.getString(OWM_RUNTIME);


            //Get the JSON object for Images
            JSONObject movieImagesObject = movieInfoObject.getJSONObject(OWM_IMAGES);

            JSONArray posterArray = movieImagesObject.getJSONArray(OWM_POSTER_ORIGINAL);
            for(int n = 0 ; n < posterArray.length(); n++){
                posterStr = posterArray.getString(n);
            }
            backdrop_original = movieImagesObject.getString(OWM_BACKDROP_ORIGINAL);

            JSONArray genresArray = movieInfoObject.getJSONArray(OWM_GENRES);
            ArrayList<String> genreStringArray = new ArrayList<String>();
            for(int g = 0, count = genresArray.length(); g< count; g++)
            {
                try {
                    genreStringArray.add(genresArray.getString(g));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            Log.v(LOG_TAG, "Movie Genres Length: " + genreStringArray.size());
//            Log.v(LOG_TAG, "Movie Genres : " + genreStringArray);

            // Insert the movie into the database.
            addMovie(cp_id, title, status, year, quality, tagline, plot, imdb, runtime, posterStr, backdrop_original, genreStringArray);
//            Log.v(LOG_TAG, "Movie iD Added: " + movieID);
            resultStrs[i] = title + " - " + year;


        }

//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Movie Title: " + s);
//            }
        return resultStrs;
    }

//    private void DownloadFromUrl(String fileName, String urlStr)
//    {
//        Bitmap posterImage = null;
//        try
//        {
////            File f=new File("", fileName + ".jpeg");
////            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            URL url = new URL(urlStr.replaceAll(" ", "%20"));
//            File file = new File(fileName + ".jpeg");
//            URLConnection ucon = url.openConnection();
//            InputStream is = ucon.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            ByteArrayBuffer baf = new ByteArrayBuffer(50);
//            int current = 0;
//            while ((current = bis.read()) != -1)
//            {
//                baf.append((byte) current);
//            }
//
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(baf.toByteArray());
//            Log.v(LOG_TAG, "file written to internal storage");
//            fos.close();
//        }
//        catch (IOException e)
//        {
//            Log.e("download", e.getMessage());
//        }
//    }

    private String saveToInternalStorage(Bitmap bitmapImage, String filename){

//        Bitmap thumbnail = Bitmap.createScaledBitmap(bitmapImage, 160, 240, false);
        ContextWrapper cw = new ContextWrapper(this.mContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File imagePath=new File(directory, filename+".jpg");
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(imagePath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
//            Log.v("FetchWanted Task - Save to disk", "Nothing to save");
//            e.printStackTrace();
        }

//        String imagePath = "imageDir"+"/"+filename + ".jpg";
//        Log.v("Filename IS ", imagePath);
//        return imagePath;
        return directory.getAbsolutePath();
    }

    @Override
    protected String[] doInBackground(String... params) {

        // If there's no API Key, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String wantedJsonStr = null;
        String api_key = params[0];
        String host_address = params[1];
        String server_port = params[2];
        String api_call = "media.list";
        String status_param = "active";

        try {
            // Construct the URL for the CouchPotato Call

            URL url = new URL(
                    "http://"+host_address+":"+server_port+"/api/"+api_key+"/"+api_call+"?status="+status_param);

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
            wantedJsonStr = buffer.toString();
//            Log.v(LOG_TAG, "Wanted Movies string: " + wantedJsonStr);
        } catch (IOException e) {
            Log.e("WantedFragment", "Error ", e);
            Toast.makeText(mContext, "Unable to connect load movie list", Toast.LENGTH_LONG).show();
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
                    Log.e("WantedFragment", "Error closing stream", e);
                }
            }
        }
        try {
            return getWantedMovieDataFromJson(wantedJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    protected void onPostExecute(String[] result) {
//        if (result != null) {
//            mWantedMovieAdapter.clear();
//            for(String wantedMovieStr : result) {
//                mWantedMovieAdapter.add(wantedMovieStr);
//            }
//            // New data is back from the server.  Hooray!
//        }
//    }
}
