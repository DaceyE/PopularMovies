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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class TMDbContentProvider extends ContentProvider {

    private static final String TAG = "popularmovies " + TMDbContentProvider.class.getSimpleName();

    private static UriMatcher mUriMatcher;
    private static CustomSQLiteOpenHelper mCustomSQLiteOpenHelper;

    //TODO I think this string is in the wrong place. Should be in contract class
    public static String AUTHORITY = "com.example.android.ennis.barrett.popularmovies.data";
    private static final int MOVIES = 5;
    private static final int VIDEOS = 15;
    private static final int REVIEWS = 25;


    //TODO read up on opinions of using static initialisation block in android.  Also reveiw best practices for them in java
    static {
        //TODO Read up on creating the uri for the *_ROW.  The udacity webcast & other examples don't seem to make sense because they throw out the selection and selectionArgs[] input
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, TMDbContract.Movies.TABLE_NAME, MOVIES);
        mUriMatcher.addURI(AUTHORITY, TMDbContract.Videos.TABLE_NAME, VIDEOS);
        mUriMatcher.addURI(AUTHORITY, TMDbContract.Reviews.TABLE_NAME, REVIEWS);
    }


    @Override
    public boolean onCreate() {
        mCustomSQLiteOpenHelper = new CustomSQLiteOpenHelper(getContext());

        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        int match = mUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case MOVIES:
                cursor = mCustomSQLiteOpenHelper.getReadableDatabase().query(
                        TMDbContract.Movies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case VIDEOS:
                cursor = mCustomSQLiteOpenHelper.getReadableDatabase().query(
                        TMDbContract.Videos.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case REVIEWS:
                cursor = mCustomSQLiteOpenHelper.getReadableDatabase().query(
                        TMDbContract.Reviews.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        return cursor;
    }

    //TODO Implement getType
    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                break;
            case VIDEOS:
                break;
            case REVIEWS:
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        return null;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "Value inserted: " + values.toString());

        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        long _id;
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                _id = database.insert(TMDbContract.Movies.TABLE_NAME, null, values);
                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return TMDbContract.buildURI(uri, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
            case VIDEOS:
                _id = database.insert(TMDbContract.Videos.TABLE_NAME, null, values);
                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return TMDbContract.buildURI(uri, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
            case REVIEWS:
                _id = database.insert(TMDbContract.Reviews.TABLE_NAME, null, values);
                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return TMDbContract.buildURI(uri, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    /**
     * A more efficient way to insert large amounts of data into the provider.
     * @param uri
     * @param values The ContentValues to insert
     * @return The number of successfully inserted rows
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        int amountInserted = 0;
        int match = mUriMatcher.match(uri);

        long _id;

        switch (match) {
            case MOVIES:
                database.beginTransaction();
                for (ContentValues content : values) {
                    Log.d(TAG, "Value inserted: " + values.toString());
                    _id = database.insert(TMDbContract.Movies.TABLE_NAME, null, content);
                    if (_id > 0) amountInserted++;
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                break;
            case VIDEOS:
                database.beginTransaction();
                for (ContentValues content : values) {
                    Log.d(TAG, "Value inserted: " + values.toString());
                    _id = database.insert(TMDbContract.Videos.TABLE_NAME, null, content);
                    if (_id > 0) amountInserted++;
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                break;
            case REVIEWS:
                database.beginTransaction();
                for (ContentValues content : values) {
                    Log.d(TAG, "Value inserted: " + values.toString());
                    _id = database.insert(TMDbContract.Reviews.TABLE_NAME, null, content);
                    if (_id > 0) amountInserted++;
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                break;
            default:
                //TODO consider calling super instead.  This would let the method be usable even if I haven't overiden it for all URIs
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (amountInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
        return amountInserted;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int numberUpdated = 0;
        switch (match) {
            case MOVIES:
                numberUpdated = database.delete(TMDbContract.Movies.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                numberUpdated = database.delete(TMDbContract.Videos.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                numberUpdated = database.delete(TMDbContract.Reviews.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (numberUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberUpdated;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int numberUpdated = 0;
        switch (match) {
            case MOVIES:
                numberUpdated = database.update(TMDbContract.Movies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                numberUpdated = database.update(TMDbContract.Videos.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                numberUpdated = database.update(TMDbContract.Reviews.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (numberUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberUpdated;
    }

}
