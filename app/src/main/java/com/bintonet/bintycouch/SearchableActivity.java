package com.bintonet.bintycouch;

/**
 * Created by Darren on 15/08/2014.
 */

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class SearchableActivity extends ListActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MY", "search activity triggered");
    }

}


