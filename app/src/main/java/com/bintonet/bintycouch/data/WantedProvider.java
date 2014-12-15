package com.bintonet.bintycouch.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Darren on 07/08/2014.
 */
public class WantedProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    private static final int WANTED = 100;
    private static final int WANTED_WITH_CP_ID = 101;
    private static final int WANTED_WITH_TITLE = 102;

    private static final SQLiteQueryBuilder sWantedByCPIDQueryBuilder;

    static{
        sWantedByCPIDQueryBuilder = new SQLiteQueryBuilder();
        sWantedByCPIDQueryBuilder.setTables(
                MovieContract.WantedEntry.TABLE_NAME);
    }

    private static final String sCPIDSelection =
            MovieContract.WantedEntry.COLUMN_CP_ID + " = ? ";

    private static final String sTitleSelection =
            MovieContract.WantedEntry.COLUMN_TITLE + " = ? ";

    private Cursor getWantedMovieByCPID(Uri uri, String[] projection, String sortOrder) {
        String cp_id = MovieContract.WantedEntry.getWantedCPIDFromUri(uri);
        Log.v("BintyCouch - getWantedMovieByCPID", cp_id);

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{cp_id};
        selection = sCPIDSelection;

        return sWantedByCPIDQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_WANTED, WANTED);
        matcher.addURI(authority, MovieContract.PATH_WANTED + "/*", WANTED_WITH_CP_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*"
            case WANTED_WITH_CP_ID: {
                Log.v("BintyCouch - Wanted Provider", "WANTED_WITH_CP_ID");
                retCursor = getWantedMovieByCPID(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case WANTED: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.WantedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WANTED:

                return MovieContract.WantedEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v("BintyCouch - Wanted Provider", "INSERT !!!!!!");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case WANTED: {
                long _id = db.insert(MovieContract.WantedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.WantedEntry.buildWantedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v("BintyCouch - Wanted Provider", "DELETE !!!!!!!!");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case WANTED:
                rowsDeleted = db.delete(
                        MovieContract.WantedEntry.TABLE_NAME, selection, selectionArgs);
                Log.v("Movie Deleted", selection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v("BintyCouch - Wanted Provider", "UPDATE");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case WANTED:
                rowsUpdated = db.update(MovieContract.WantedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WANTED:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.WantedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
