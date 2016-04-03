package com.example.android.ennis.barrett.popularmovies.asynchronous;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.ennis.barrett.popularmovies.BuildConfig;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by Barrett on 2/16/2016.
 */
public class FetchTMDb extends AsyncTask<Integer, Void, Void> {

    private static final String TAG = "popularmovies " + FetchTMDb.class.getSimpleName();

    public static final int MOVIES_POPULAR = 0;
    public static final int MOVIES_TOP_RATED = 1;

    private Context mContext;

    public FetchTMDb(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(Integer... params) {
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
        switch (params[0]) {
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

            if (inputStream == null) return null;
            reader = new BufferedReader(new InputStreamReader(inputStream));


            //Make the string more readable
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }


            if (stringBuffer.length() == 0) {
                Log.e(TAG + "doInBackground", "empty string");
                return null;
            }


            rawJSON = stringBuffer.toString();
            Log.d(TAG + "doInBackground", rawJSON);


            switch (params[0]) {
                case MOVIES_POPULAR:
                    storeJSONMovies(rawJSON.toString(), params[0]);
                    break;
                case MOVIES_TOP_RATED:
                    storeJSONMovies(rawJSON.toString(), params[0]);
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

        return null;
    }//doInBackground


    //TODO store the overview.  Currently it is not stored because it need to be filtered for any ' in the string.  Without filtering the app will be unstable
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

            ContentResolver contentResolver = mContext.getContentResolver();
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

} //End of class



