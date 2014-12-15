package com.bintonet.bintycouch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Darren on 07/08/2014.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 6;

    private static final String DATABASE_NAME = "cp_movies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WANTED_TABLE = "CREATE TABLE " + MovieContract.WantedEntry.TABLE_NAME + " (" +

                MovieContract.WantedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                MovieContract.WantedEntry.COLUMN_CP_ID + " TEXT NOT NULL, " +
                MovieContract.WantedEntry.COLUMN_YEAR + " TEXT, " +
                MovieContract.WantedEntry.COLUMN_TITLE + " TEXT, " +
                MovieContract.WantedEntry.COLUMN_STATUS + " TEXT," +
                MovieContract.WantedEntry.COLUMN_QUALITY + " TEXT," +
                MovieContract.WantedEntry.COLUMN_RUNTIME + " TEXT," +
                MovieContract.WantedEntry.COLUMN_TAGLINE + " TEXT," +
                MovieContract.WantedEntry.COLUMN_PLOT + " TEXT," +
                MovieContract.WantedEntry.COLUMN_IMDB + " TEXT," +
                MovieContract.WantedEntry.COLUMN_POSTER_ORIGINAL + " TEXT," +
                MovieContract.WantedEntry.COLUMN_BACKDROP_ORIGINAL + " TEXT,"+
                MovieContract.WantedEntry.COLUMN_GENRES + " TEXT,"+




                // Set up the location column as a foreign key to location table.
//                " FOREIGN KEY (" + MovieContract.WantedEntry.COLUMN_CP_ID + ") REFERENCES " +
//                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieContract.WantedEntry.COLUMN_CP_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WANTED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.WantedEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
