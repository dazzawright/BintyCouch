package com.bintonet.bintycouch;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.bintonet.bintycouch.data.MovieContract;

import java.io.File;
import java.io.FileInputStream;

public class DetailActivity extends Activity {

    public static final String MOVIE_KEY = "cp_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        public static final String MOVIE_KEY = "cp_id";


        private ShareActionProvider mShareActionProvider;
        private String mMovie;
        private String mDetails;

        private static final int DETAIL_LOADER = 0;

        private static final String[] WANTED_MOVIE_COLUMNS = {
                MovieContract.WantedEntry._ID,
                MovieContract.WantedEntry.COLUMN_CP_ID,
                MovieContract.WantedEntry.COLUMN_TITLE,
                MovieContract.WantedEntry.COLUMN_YEAR,
                MovieContract.WantedEntry.COLUMN_STATUS,
                MovieContract.WantedEntry.COLUMN_QUALITY,
                MovieContract.WantedEntry.COLUMN_RUNTIME,
                MovieContract.WantedEntry.COLUMN_TAGLINE,
                MovieContract.WantedEntry.COLUMN_PLOT,
                MovieContract.WantedEntry.COLUMN_IMDB,
                MovieContract.WantedEntry.COLUMN_POSTER_ORIGINAL,
                MovieContract.WantedEntry.COLUMN_BACKDROP_ORIGINAL
        };

        public DetailFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null || !intent.hasExtra(MOVIE_KEY)) {
                return null;
            }
            String movieTitle = intent.getStringExtra(MOVIE_KEY);
//            Log.v(LOG_TAG, movieTitle);
//            movieTitle = "93a1b6829cef4bca95ae15ef9167e521";
//            Log.v(LOG_TAG, movieTitle);

            String sortOrder = MovieContract.WantedEntry.COLUMN_TITLE + " ASC";
            String URL = "content://com.bintonet.bintycouch/wanted/"+movieTitle;
            Uri wantedMovieUri = Uri.parse(URL);

            return new CursorLoader(
                    getActivity(),
                    wantedMovieUri,
                    WANTED_MOVIE_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");

//            Log.v(LOG_TAG, );
            if (!data.moveToFirst()) { return; }
            String titleString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_TITLE));
            String yearString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_YEAR));
            String statusString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_STATUS));
            String runtimeString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_RUNTIME));
            String qualityString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_QUALITY));
//            String genreString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_GENRES));
            String tagLineString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_TAGLINE));
            String plotString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_PLOT));
            String backdropString = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_BACKDROP_ORIGINAL));
            String thumbFilename = data.getString(data.getColumnIndex(MovieContract.WantedEntry.COLUMN_CP_ID))+"_full.jpg";

            new LoadImageTask((ImageView) this.getView().findViewById(R.id.detail_icon))
                    .execute("/data/data/com.bintonet.bintycouch/app_imageDir", thumbFilename);

//            Log.v(LOG_TAG, titleString);
//            Log.v(LOG_TAG, yearString);
//            Log.v(LOG_TAG, tagLineString);
//            Log.v(LOG_TAG, plotString);
//            Log.v(LOG_TAG, backdropString);
//            Log.v(LOG_TAG, runtimeString);
//            Log.v(LOG_TAG, qualityString);

            ((TextView) getView().findViewById(R.id.detail_title_textview))
                    .setText(titleString);
            ((TextView) getView().findViewById(R.id.detail_year_textview))
                    .setText(yearString);
            ((TextView) getView().findViewById(R.id.detail_tagline_textview))
                    .setText(tagLineString);
            ((TextView) getView().findViewById(R.id.detail_plot_textview))
                    .setText(plotString);
            ((TextView) getView().findViewById(R.id.detail_runtime_textview))
                    .setText(runtimeString+ " minutes");
            ((TextView) getView().findViewById(R.id.detail_quality_textview))
                    .setText(qualityString);
//            ((TextView) getView().findViewById(R.id.detail_backdrop_original_textview))
//                    .setText(backdropString);



        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
//    private static Bitmap LoadImageTask (String path, String filename){
//
//            File f=new File(path, filename);
//            Bitmap bitmap = null;
//            try {
//                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
////                final int THUMBNAIL_SIZE = 80;
////                bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
////                e.printStackTrace();
//            }
//            return bitmap;
//        }

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... params) {
            File f=new File(params[0], params[1]);

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
//                final int THUMBNAIL_SIZE = 80;
//                bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
//                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
