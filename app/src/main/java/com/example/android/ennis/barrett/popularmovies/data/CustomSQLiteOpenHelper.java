/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.ennis.barrett.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Manages the database behind the TMDbContentProvider
 */
public class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

    private final String TAG = "dacey_" + getClass();
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "popular movies db";

    public CustomSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called by the system on the creation of the database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO fix col properties
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

    /**
     * Handles upgrading the database, currently deletes the entire database.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade executed");

        db.execSQL("DROP TABLE IF EXISTS " + TMDbContract.Movies.TABLE_NAME);
        Log.i(TAG, "TABLES DROPPED");

        onCreate(db);
    }

}//end of class
