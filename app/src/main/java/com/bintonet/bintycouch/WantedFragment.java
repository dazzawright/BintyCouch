package com.bintonet.bintycouch;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bintonet.bintycouch.data.MovieContract;

/**
 * Created by Darren on 05/08/2014.
 */
public class WantedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

//    private SimpleCursorAdapter mWantedMovieAdapter;

    private WantedAdapter mWantedMovieAdapter;
    private static final int WANTED_LOADER = 0;
    private String mWantedMovie;

    private static final String[] WANTED_MOVIE_COLUMNS = {
            MovieContract.WantedEntry._ID,
            MovieContract.WantedEntry.COLUMN_CP_ID,
            MovieContract.WantedEntry.COLUMN_TITLE,
            MovieContract.WantedEntry.COLUMN_YEAR,
            MovieContract.WantedEntry.COLUMN_STATUS,
            MovieContract.WantedEntry.COLUMN_TAGLINE,
            MovieContract.WantedEntry.COLUMN_PLOT,
            MovieContract.WantedEntry.COLUMN_IMDB,
            MovieContract.WantedEntry.COLUMN_POSTER_ORIGINAL,
            MovieContract.WantedEntry.COLUMN_BACKDROP_ORIGINAL
    };

    // These indices are tied to WANTED_MOVIE_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int _ID = 0;
    public static final int COL_CP_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_YEAR = 3;
    public static final int COL_STATUS = 4;
    public static final int COL_TAGLINE = 5;
    public static final int COL_PLOT = 6;
    public static final int COL_IMDB = 7;
    public static final int COL_POSTER_ORIGINAL = 8;
    public static final int COL_BACKDROP_ORIGINAL = 9;

    public WantedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wantedfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWanted();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            mWantedMovieAdapter = new WantedAdapter(getActivity(), null, 0);


            // The ArrayAdapter will take data from a source and
            // use it to populate the ListView it's attached to.
//        mWantedMovieAdapter = new WantedAdapter(getActivity(), null, 0);

//        mWantedMovieAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.list_item_wanted,
//                null,
//                // the column names to use to fill the textviews
//                new String[]{MovieContract.WantedEntry.COLUMN_TITLE,
//                        MovieContract.WantedEntry.COLUMN_YEAR
////                        MovieContract.WantedEntry.COLUMN_TAGLINE,
////                        MovieContract.WantedEntry.COLUMN_PLOT,
////                        MovieContract.WantedEntry.COLUMN_BACKDROP_ORIGINAL
//                },
//                // the textviews to fill with the data pulled from the columns above
//                new int[]{R.id.list_item_title_textview,
//                        R.id.list_item_year_textview
//                },
//                0
//        );
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_wanted);
            listView.setAdapter(mWantedMovieAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = mWantedMovieAdapter.getCursor();
                    if (cursor != null && cursor.moveToPosition(position)) {

                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra(DetailActivity.MOVIE_KEY, cursor.getString(1));
                        startActivity(intent);
                    }
                }
            });

            return rootView;
        }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(WANTED_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWanted() {
        String api_key = Utility.getAPIKey(getActivity());
        String host_address = Utility.getHostAddress(getActivity());
        String server_port = Utility.getServerPort(getActivity());
//        Log.v("Wanted Fragment - Fetching Keys", api_key);
//        Log.v("Wanted Fragment - Fetching Keys", host_address);
//        Log.v("Wanted Fragment - Fetching Keys", server_port);
        if(api_key == "" || host_address == "" || server_port == ""){
            Toast.makeText(this.getActivity(), "East tiger, you need to complete the server settings first", Toast.LENGTH_LONG);
        }
        else{
            new FetchWantedTask(getActivity()).execute(api_key, host_address, server_port);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWanted();
    }
//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Sort order:  Ascending, by date.
        String sortOrder = MovieContract.WantedEntry.COLUMN_TITLE + " ASC";
        String URL = "content://com.bintonet.bintycouch/wanted/";
        Uri wantedMovieUri = Uri.parse(URL);
//        Toast.makeText(getActivity(), URL, Toast.LENGTH_SHORT).show();

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
        mWantedMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWantedMovieAdapter.swapCursor(null);
    }
}
