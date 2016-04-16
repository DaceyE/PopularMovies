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
package com.example.android.ennis.barrett.popularmovies.asynchronous;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.ennis.barrett.popularmovies.BuildConfig;
import com.example.android.ennis.barrett.popularmovies.R;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class TMDbSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "popularmovies " + TMDbSyncAdapter.class.getSimpleName();

    //TODO move my networking code to a separate class (fetch and store methods)
    public static final int MOVIES_POPULAR = 0;
    public static final int MOVIES_TOP_RATED = 1;

    public static final int SYNC_INTERVAL = 72000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    /**
     * Set up the sync adapter
     */
    TMDbSyncAdapter(Context context, boolean bool) {
        super(context, bool);
        Log.v(TAG, "Adapter Constructed");
    }

    //TODO implement (Context context, boolean autoInitialize, boolean allowParallelSyncs) to allow compatibility with Android 3.0 (11) and later platform versions

    /*
    * Calls any data transfer code needed by the app.
    */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {

        Log.i(TAG, "onPerformSync");
        int[] ids = fetchMovies(MOVIES_POPULAR);
        fetchDetails(ids);
        ids = fetchMovies(MOVIES_TOP_RATED);
        fetchDetails(ids);

    }


    /**
     * makes the network call to fetch the data and the calls storeJsonMovies
     * @param type Flag for the try of movie to fetch.
     * @return Returns an array of ids of the movies that were inserted
     */
    private int[] fetchMovies(int type) {
        int[] ids = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        String rawJSON = null;
        String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
        final String API_KEY_PARAM = "api_key";

        /*
        example urls
        http://api.themoviedb.org/3/movie/top_rated?api_key=xxxxxxx
        http://api.themoviedb.org/3/movie/popular?api_key=xxxxxxxx
        */
        //TODO build Uri programmatically using APIs
        switch (type) {
            case MOVIES_POPULAR:
                MOVIE_BASE_URL += "/popular" + "?";
                break;
            case MOVIES_TOP_RATED:
                MOVIE_BASE_URL += "/top_rated" + "?";
                break;
            default:
                throw new IllegalArgumentException("Unrecognized id passed as params[0].  Params[0] must be the id for the url.  See static constants of this class");
        }


        try {

            Uri droidUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDb_API_KEY)
                    .build();

            Log.v(TAG, "URL used: " + droidUri.toString());

            URL url = new URL(droidUri.toString());  //java.net Malformed uri exception

            connection = (HttpURLConnection) url.openConnection(); //java.io.  IO exception
            connection.setRequestMethod("GET"); //java.net.ProtocolException && unnecessary
            connection.connect(); //java.io.IOExecption

            InputStream inputStream = connection.getInputStream(); // java.io.IOException
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return ids;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            //Make the string more readable
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0) {
                Log.e(TAG + "doInBackground", "empty string");
                return ids;
            }

            rawJSON = stringBuffer.toString();
            Log.d(TAG + "doInBackground", rawJSON);

            switch (type) {
                case MOVIES_POPULAR:
                    ids = storeJsonMovies(rawJSON.toString(), type);
                    break;
                case MOVIES_TOP_RATED:
                    ids = storeJsonMovies(rawJSON.toString(), type);
                    break;
            }

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                    e.printStackTrace();
                }
            }
        }
        return ids;
    }




    /**
     * Parses and stores the JSON data in the TMDbContentProvider
     * @param dataJSON The JSON data from server
     * @param type Flag for the try of movie to fetch.  Should match flag used in fetchMovies
     * @throws JSONException
     */
    private int[] storeJsonMovies(String dataJSON, int type) throws JSONException {
        //TODO use ContentProviderClient instead of ContentResolver
        try {
            JSONObject moviesJSON = new JSONObject(dataJSON);
            JSONArray results = moviesJSON.getJSONArray("results");
            final int resultLength = results.length();
            ContentValues[] contentValues = new ContentValues[resultLength];
            int[] ids = new int[resultLength];
            int isPopular;
            int isTopRated;

            switch (type) {
                case MOVIES_POPULAR:
                    isPopular = 1;
                    isTopRated = 0;
                    break;
                case MOVIES_TOP_RATED:
                    isPopular = 0;
                    isTopRated = 1;
                    break;
                default:
                    isPopular = 0;
                    isTopRated = 0;
            }

            for (int i = 0; i < resultLength; i++) {
                JSONObject movie = results.getJSONObject(i);
                contentValues[i] = new ContentValues();
                contentValues[i].put(TMDbContract.Movies.ORIGINAL_TITLE,
                        movie.getString(TMDbContract.Movies.ORIGINAL_TITLE));

                contentValues[i].put(TMDbContract.Movies.OVERVIEW,
                        movie.getString(TMDbContract.Movies.OVERVIEW));
                contentValues[i].put(TMDbContract.Movies.POSTER,
                        movie.getString(TMDbContract.Movies.POSTER));
                contentValues[i].put(TMDbContract.Movies.RELEASE_DATE,
                        movie.getString(TMDbContract.Movies.RELEASE_DATE));
                contentValues[i].put(TMDbContract.Movies.POPULARITY,
                        movie.getDouble(TMDbContract.Movies.POPULARITY));
                contentValues[i].put(TMDbContract.Movies.VOTE_AVERAGE,
                        movie.getDouble(TMDbContract.Movies.VOTE_AVERAGE));
                contentValues[i].put(TMDbContract.Movies.MOVIE_ID,
                        movie.getInt(TMDbContract.Movies.MOVIE_ID));
                //TODO read up on how to have the col initialized to 0 by defualt
                contentValues[i].put(TMDbContract.Movies.IS_POPULAR, isPopular);
                contentValues[i].put(TMDbContract.Movies.IS_TOP_RATED, isTopRated);

                ids[i] = movie.getInt(TMDbContract.Movies.MOVIE_ID);
            }

            ContentResolver contentResolver = getContext().getContentResolver();
            //TODO Read Uri APIs
            //TODO declare URI constants in contract
            Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY
                    + "/" + TMDbContract.Movies.TABLE_NAME);
            int numInserted = contentResolver.bulkInsert(uriTMDb, contentValues);

            if (numInserted != resultLength) {
                Log.e(TAG, "Not all of the result were inserted.\n Amount inserted: " + numInserted
                        + "\nAmount from server: " + resultLength);
            }

            return ids;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    } //storeJsonMovies

    private void fetchDetails(int[] movieIds) {
        /*
        example urls
        http://api.themoviedb.org/3/movie/MOVIE_ID/videos?api_key=XXXXXXX
        http://api.themoviedb.org/3/movie/MOVIE_ID/reviews?api_key=XXXXXX
        */

        String[] rawJsonVideos = new String[movieIds.length];
        String[] rawJsonReviews = new String[movieIds.length];

        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
        String apiKey = "ad9eabc5e930874cca7cc84910beea89";
        final String API_KEY_PARAM = "api_key";
        String myurl;
        Uri droidUri;

        for (int i = 0; i < movieIds.length; i++) {
            myurl = MOVIE_BASE_URL;
            myurl += "/" + movieIds[i] + "/videos" + "?";
            droidUri = Uri.parse(myurl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            rawJsonVideos[i] = fetch(droidUri);

            myurl = MOVIE_BASE_URL;
            myurl += "/" + movieIds[i] + "/reviews" + "?";
            droidUri = Uri.parse(myurl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            rawJsonReviews[i] = fetch(droidUri);
        }
        Log.d(TAG, "Number of Videos objects: " + rawJsonVideos.length);
        Log.d(TAG, "Number of Reviews objects: " + rawJsonReviews.length);
        try {
            storeJsonVideos(rawJsonVideos);
            storeJsonReviews(rawJsonReviews);
        } catch (JSONException j){
            Log.e(TAG, "JSON exception...also this was temp code...");
        }

    }

    private String fetch(Uri uri) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String rawJSON = "";

        Log.d(TAG, uri.toString());
        try {
            URL url = new URL(uri.toString());  //java.net Malformed uri exception

            connection = (HttpURLConnection) url.openConnection(); //java.io.  IO exception
            connection.setRequestMethod("GET"); //java.net.ProtocolException && unnecessary
            connection.connect(); //java.io.IOExecption

            InputStream inputStream = connection.getInputStream(); // java.io.IOException
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return rawJSON;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            //Make the string more readable
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            rawJSON = stringBuffer.toString();
            Log.d(TAG + "doInBackground", rawJSON);

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        //} catch (JSONException e) {
            //Log.e(TAG, e.getMessage(), e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        return rawJSON;
    }

    /**
     * Parses and stores the JSON data in the TMDbContentProvider
     * @param jsonDataArray An array of raw JSON strings
     * @throws JSONException
     */
    private void storeJsonVideos(String[] jsonDataArray) throws JSONException {
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(40);

        for (String dataJSON : jsonDataArray) {
            try {
                JSONObject videosJSON = new JSONObject(dataJSON);
                JSONArray results = videosJSON.getJSONArray("results");
                int movieId = videosJSON.getInt(TMDbContract.Movies.MOVIE_ID);
                int resultLength = results.length();
                ContentValues values;

                for (int i = 0; i < resultLength; i++) {
                    JSONObject video = results.getJSONObject(i);
                    values = new ContentValues();

                    values.put(TMDbContract.Videos.NAME,
                            video.getString(TMDbContract.Videos.NAME));
                    values.put(TMDbContract.Videos.KEY,
                            video.getString(TMDbContract.Videos.KEY));
                    //TODO check what type of video and store the associated value (see contract)
                    values.put(TMDbContract.Videos.TYPE, 0);
                    values.put(TMDbContract.Videos.VIDEO_ID,
                            video.getString(TMDbContract.Videos.VIDEO_ID));
                    values.put(TMDbContract.Videos.MOVIE_IDS, movieId);

                    String printdata = video.getString(TMDbContract.Videos.VIDEO_ID);
                    Log.d(TAG, "videoID " + printdata);
                    contentValuesArrayList.add(values);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        ContentResolver contentResolver = getContext().getContentResolver();
        ContentValues[] contentValues = new ContentValues[contentValuesArrayList.size()];
        contentValues = contentValuesArrayList.toArray(contentValues);

        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY
                + "/" + TMDbContract.Videos.TABLE_NAME);
        int numInserted = contentResolver.bulkInsert(uriTMDb, contentValues);

        if (numInserted != contentValues.length) {
            Log.e(TAG, "Not all of the result were inserted.\n Amount inserted: " + numInserted
                    + "\nAmount from server: " + contentValues.length);
        }
    } //storeJsonVideos

    /**
     * Parses and stores the JSON data in the TMDbContentProvider
     * @param jsonDataArray An array of raw JSON strings
     * @throws JSONException
     */
    private void storeJsonReviews(String[] jsonDataArray) throws JSONException {
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(40);

        for (String dataJSON : jsonDataArray) {
            try {
                JSONObject reviewsJSON = new JSONObject(dataJSON);
                JSONArray results = reviewsJSON.getJSONArray("results");
                int movieId = reviewsJSON.getInt(TMDbContract.Movies.MOVIE_ID);
                int resultLength = results.length();

                ContentValues value;

                for (int i = 0; i < resultLength; i++) {
                    JSONObject movie = results.getJSONObject(i);
                    value = new ContentValues();
                    value.put(TMDbContract.Reviews.AUTHOR,
                            movie.getString(TMDbContract.Reviews.AUTHOR));
                    value.put(TMDbContract.Reviews.REVIEW_CONTENT,
                            movie.getString(TMDbContract.Reviews.REVIEW_ID));
                    value.put(TMDbContract.Videos.VIDEO_ID,
                            movie.getString(TMDbContract.Videos.VIDEO_ID));
                    value.put(TMDbContract.Reviews.MOVIE_IDS, movieId);
                    contentValuesArrayList.add(value);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        ContentResolver contentResolver = getContext().getContentResolver();
        ContentValues[] contentValues = new ContentValues[contentValuesArrayList.size()];
        contentValues = contentValuesArrayList.toArray(contentValues);

        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY
                + "/" + TMDbContract.Reviews.TABLE_NAME);
        int numInserted = contentResolver.bulkInsert(uriTMDb, contentValues);

        if (numInserted != contentValues.length) {
            Log.e(TAG, "Not all of the result were inserted.\n Amount inserted: " + numInserted
                    + "\nAmount from server: " + contentValues.length);
        }
    } //storeJsonReviews



    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.v(TAG, "configurePeriodicSync");
        Account account = getSyncAccount(context);
        String authority = "com.example.android.ennis.barrett.popularmovies.data";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Log.i(TAG, "syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                "com.example.android.ennis.barrett.popularmovies.data", bundle);
    }

    public static Account getSyncAccount(Context context) {
        Log.v(TAG, "getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),
                "com.example.android.ennis.barrett.popularmovies.asynchronous.Authenticator");

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.v(TAG, "addAccountExplicitly");
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.i(TAG, "onAccountCreated");

        TMDbSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount,
                "com.example.android.ennis.barrett.popularmovies.data", true);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(TAG, "initializeSyncAdapter");
        getSyncAccount(context);
    }

}
