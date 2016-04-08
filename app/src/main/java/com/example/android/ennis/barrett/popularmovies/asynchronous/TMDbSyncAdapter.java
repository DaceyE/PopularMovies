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

public class TMDbSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "popularmovies " + TMDbSyncAdapter.class.getSimpleName();

    public static final int MOVIES_POPULAR = 0;
    public static final int MOVIES_TOP_RATED = 1;

    public static final int SYNC_INTERVAL = 72000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    //Placeholder
    TMDbSyncAdapter(Context context, boolean bool) {
        super(context, bool);
        Log.i(TAG, "Adapter Constructed");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e(TAG, "onPerformSync");
        fetchMovies(MOVIES_POPULAR);
        fetchMovies(MOVIES_TOP_RATED);
    }

    //TODO use ContentProviderClient instead
    private void fetchMovies(int type) {
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

        //TODO build Uri programmatically
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


            //TODO Fine out why these log statments don't work "Log.d(TAG + "doInBackground",....."
            //Log.d(TAG + "doInBackground", "Well I used the Uri successfully.. I hope");
            //Log.e(TAG + "doInBackground", droidUri.toString());

            Log.v(TAG, "URL used: " + droidUri.toString());

            URL url = new URL(droidUri.toString());  //java.net Malformed uri exception

            connection = (HttpURLConnection) url.openConnection(); //java.io.  IO exception
            connection.setRequestMethod("GET"); //java.net.ProtocolException && unnecessary
            connection.connect(); //java.io.IOExecption


            InputStream inputStream = connection.getInputStream(); // java.io.IOException
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) return;
            reader = new BufferedReader(new InputStreamReader(inputStream));


            //Make the string more readable
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }


            if (stringBuffer.length() == 0) {
                Log.e(TAG + "doInBackground", "empty string");
                return;
            }


            rawJSON = stringBuffer.toString();
            Log.d(TAG + "doInBackground", rawJSON);


            switch (type) {
                case MOVIES_POPULAR:
                    storeJSONMovies(rawJSON.toString(), type);
                    break;
                case MOVIES_TOP_RATED:
                    storeJSONMovies(rawJSON.toString(), type);
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
    }


    private void storeJSONMovies(String dataJSON, int type) throws JSONException {
        try {
            JSONObject moviesJSON = new JSONObject(dataJSON);
            JSONArray results = moviesJSON.getJSONArray("results");
            final int resultLength = results.length();
            ContentValues[] contentValues = new ContentValues[resultLength];
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
                contentValues[i].put(TMDbContract.Movies.ORIGINAL_TITLE, movie.getString(TMDbContract.Movies.ORIGINAL_TITLE));

                //TODO fix the overview by dealing with the ' in the strings
                contentValues[i].put(TMDbContract.Movies.OVERVIEW, movie.getString(TMDbContract.Movies.OVERVIEW));
                contentValues[i].put(TMDbContract.Movies.POSTER, movie.getString(TMDbContract.Movies.POSTER));
                contentValues[i].put(TMDbContract.Movies.RELEASE_DATE, movie.getString(TMDbContract.Movies.RELEASE_DATE));
                contentValues[i].put(TMDbContract.Movies.POPULARITY, movie.getDouble(TMDbContract.Movies.POPULARITY));
                contentValues[i].put(TMDbContract.Movies.VOTE_AVERAGE, movie.getDouble(TMDbContract.Movies.VOTE_AVERAGE));
                contentValues[i].put(TMDbContract.Movies.MOVIE_ID, movie.getInt(TMDbContract.Movies.MOVIE_ID));

                //TODO read up on how to have the col initialized to 0 by defualt
                contentValues[i].put(TMDbContract.Movies.IS_POPULAR, isPopular);
                contentValues[i].put(TMDbContract.Movies.IS_TOP_RATED, isTopRated);
            }

            ContentResolver contentResolver = getContext().getContentResolver();
            //TODO Read Uri APIs
            //TODO declair URI constants in contract
            Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/" + TMDbContract.Movies.TABLE_NAME);
            int numInserted = contentResolver.bulkInsert(uriTMDb, contentValues);

            if (numInserted != resultLength)
                Log.e(TAG, "Not all of the result were inserted.\n Amount inserted: " + numInserted + "\nAmount from server: " + resultLength);

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    } //storeJSONMovies


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.i(TAG, "configurePeriodicSync");
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
        Log.i(TAG, "getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), "com.example.android.ennis.barrett.popularmovies.asynchronous.Authenticator");

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
            Log.i(TAG, "getSyncAccount if");
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.i(TAG, "addAccountExplicitly");
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        Log.i(TAG, "getSyncAccount after if");
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.i(TAG, "onAccountCreated");
        /*
         * Since we've created an account
         */
        TMDbSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, "com.example.android.ennis.barrett.popularmovies.data", true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(TAG, "initializeSyncAdapter");
        getSyncAccount(context);
    }

}
