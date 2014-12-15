package com.bintonet.bintycouch.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Darren on 07/08/2014.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.bintonet.bintycouch";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.bintonet.bintycouch/wanted/ is a valid path for
    // looking at wanted data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WANTED = "wanted";


    public static final class WantedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WANTED).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WANTED;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WANTED;

        public static final String TABLE_NAME = "wanted";

        // Column with the foreign key into the location table.
        public static final String COLUMN_CP_ID = "cp_id";
        // Date, stored as Text with format yyyy
        public static final String COLUMN_YEAR = "year";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_QUALITY = "quality";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_IMDB = "imdb";
        public static final String COLUMN_POSTER_ORIGINAL = "poster_original";
        public static final String COLUMN_BACKDROP_ORIGINAL = "backdrop_original";
        public static final String COLUMN_GENRES = "genres";

        public static Uri buildWantedUri(long cp_id) {
            return ContentUris.withAppendedId(CONTENT_URI, cp_id);
        }

        public static Uri buildWantedWithCPID(String cpID) {
            return CONTENT_URI.buildUpon().appendPath(cpID).build();
        }

        public static String getWantedCPIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
