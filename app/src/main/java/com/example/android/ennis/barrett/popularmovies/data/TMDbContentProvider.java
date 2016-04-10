package com.example.android.ennis.barrett.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class TMDbContentProvider extends ContentProvider {

    private static final String TAG = "TMDbContent dacey:";

    private static UriMatcher mUriMatcher;
    private static CustomSQLiteOpenHelper mCustomSQLiteOpenHelper;

    //TODO I think this string is in the wrong place. Should be in contract class
    public static String AUTHORITY = "com.example.android.ennis.barrett.popularmovies.data";
    private static final int MOVIES = 5;
    private static final int MOVIES_ROW = 10;


    //TODO read up on opinions of using static initialisation block in android.  Also reveiw best practices for them in java
    static {
        //TODO Read up on creating the uri for the *_ROW.  The udacity webcast & other examples don't seem to make sense because they throw out the selection and selectionArgs[] input
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, TMDbContract.Movies.TABLE_NAME, MOVIES);
        mUriMatcher.addURI(AUTHORITY, TMDbContract.Movies.TABLE_NAME + "/#", MOVIES_ROW);
    }


    @Override
    public boolean onCreate() {
        mCustomSQLiteOpenHelper = new CustomSQLiteOpenHelper(getContext());

        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
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
                return cursor;
            case MOVIES_ROW: //Throws exception!
                cursor = mCustomSQLiteOpenHelper.getReadableDatabase().query(
                        TMDbContract.Movies.TABLE_NAME,
                        projection,
                        "",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }


    //TODO Implement getType
    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                break;
            case MOVIES_ROW:
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
            case MOVIES_ROW:
                //break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        int amountInserted = 0;
        int match = mUriMatcher.match(uri);

        long _id;

        switch (match) {
            case MOVIES:
                for (ContentValues content : values) {
                    Log.d(TAG, "Value inserted: " + values.toString());
                    _id = database.insert(TMDbContract.Movies.TABLE_NAME, null, content);
                    if (_id > 0) amountInserted++;
                }
                break;
            case MOVIES_ROW:
                //break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (amountInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
        return amountInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return database.delete(TMDbContract.Movies.TABLE_NAME, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mCustomSQLiteOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return database.update(TMDbContract.Movies.TABLE_NAME, values, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

}
