package com.example.android.ennis.barrett.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Barrett on 3/18/2016.
 */
public class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

    private final String TAG = "dacey_" + getClass();
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "popular movies db";


    public CustomSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    //TODO fix col properties
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "
                + TMDbContract.Movies.TABLE_NAME + " ("
                + TMDbContract.Movies.ID + " integer primary key autoincrement not null, "
                + TMDbContract.Movies.ORIGINAL_TITLE + " text, "
                + TMDbContract.Movies.OVERVIEW + " text, "
                + TMDbContract.Movies.POSTER + " text, "
                + TMDbContract.Movies.RELEASE_DATE + " text, "
                + TMDbContract.Movies.POPULARITY + " real, "
                + TMDbContract.Movies.VOTE_AVERAGE + " real, "
                + TMDbContract.Movies.IS_POPULAR + " integer, " // not null
                + TMDbContract.Movies.IS_TOP_RATED + " integer, " // not null
                + TMDbContract.Movies.IS_FAVORITE + " integer, " // not null
                + TMDbContract.Movies.MOVIE_ID + " integer " // not null, foreign key?
                + ");";


        Log.d(TAG, createTable);
        db.execSQL(createTable);
    }//onCreate


    //TODO add logic to handle database changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade executed");

        db.execSQL("DROP TABLE IF EXISTS " + TMDbContract.Movies.TABLE_NAME);
        Log.i(TAG, "TABLES DROPPED");

        onCreate(db);
    }
}//end of class
