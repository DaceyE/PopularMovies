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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.ennis.barrett.popularmovies.BuildConfig;
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
 * Utility methods for the syncAdapter.  Makes the SyncAdapter more readable by handling most code
 * needed to make the network calls in onPerformSync.
 */
public class TMDbSyncUtil {

    private static final String TAG = "popularmovies " + TMDbSyncUtil.class.getSimpleName();

    /**
     * Flag for indiacting if fetchMovies should get data from the Popular end point
     */
    public static final int MOVIES_POPULAR = 0;

    /**
     * Flag for indiacting if fetchMovies should get data from the Top Rated end point
     */
    public static final int MOVIES_TOP_RATED = 1;




    /**
     * Removes all data from themovies, thevideos, and thereviews tables.  Does not remove data
     * flagged favorite but the columns istoprated, and ispopular will be set to false, "0".
     * @param context
     */
    public static void deletePopularAndTopRated(Context context){
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues removePopAndTop = new ContentValues(2);
        removePopAndTop.put(TMDbContract.Movies.IS_POPULAR, "0");
        removePopAndTop.put(TMDbContract.Movies.IS_TOP_RATED, "0");

        contentResolver.update(TMDbContract.Movies.URI,
                removePopAndTop,
                TMDbContract.Movies.IS_FAVORITE + " = ?", new String[]{"1"});

        contentResolver.delete(TMDbContract.Movies.URI, TMDbContract.Movies.IS_POPULAR + " = ? OR " + TMDbContract.Movies.IS_TOP_RATED + " = ?", new String[]{"1","1"});

        Cursor favoriteMovieIds = getFavoriteIds(context);

        int numberOfIds = favoriteMovieIds.getCount();
        String[] movieIds = new String[numberOfIds];
        String whereClauseVideos = "";
        String whereClauseReviews = "";

        for(int i = 0; i < numberOfIds; i++){
            favoriteMovieIds.moveToNext();
            movieIds[i] = Integer.toString(
                    favoriteMovieIds.getInt(favoriteMovieIds.getColumnIndex(TMDbContract.Movies.MOVIE_ID)));
            if (i == numberOfIds - 1) {
                whereClauseVideos += TMDbContract.Videos.MOVIE_IDS + " != ? ";
                whereClauseReviews += TMDbContract.Videos.MOVIE_IDS + " != ? ";
            } else {
                whereClauseVideos += TMDbContract.Videos.MOVIE_IDS + " != ? OR ";
                whereClauseReviews += TMDbContract.Reviews.MOVIE_IDS + " != ? OR ";
            }
        }

        contentResolver.delete(TMDbContract.Videos.URI,whereClauseVideos, movieIds);
        contentResolver.delete(TMDbContract.Reviews.URI, whereClauseReviews, movieIds);
    }

    /**
     * Gets the ids of any movies marked as favorite
     * @param context
     * @return A cursor with the movie ids (from server, not _id) of favorites
     */
    public static Cursor getFavoriteIds(Context context){
        return context.getContentResolver().query(
                TMDbContract.Movies.URI,
                new String[]{TMDbContract.Movies.MOVIE_ID},
                TMDbContract.Movies.IS_FAVORITE + " = ?",
                new String[]{"1"},
                null);
    }

    /**
     * Removes all data from themovies, thevideos, and thereviews tables.
     * @param context
     */
    public static void deleteFavorite(Context context, int movieId){
        String[] whereArgs = new  String[]{Integer.toString(movieId)};

        Log.e(TAG, "deleteFavorite");
        ContentResolver contentResolver = context.getContentResolver();
        int i = contentResolver.delete(TMDbContract.Movies.URI, TMDbContract.Movies.MOVIE_ID + " = ?", whereArgs);
        Log.e(TAG, "favorite movies deleted  " + i);

        contentResolver.delete(TMDbContract.Videos.URI, TMDbContract.Videos.MOVIE_IDS + " = ?", whereArgs);
        contentResolver.delete(TMDbContract.Reviews.URI, TMDbContract.Reviews.MOVIE_IDS + " = ?", whereArgs);
    }

    /**
     * Gives the number of movies in db that are popular or top rated.
     * @param context
     * @return the amount of movies marked as top rated and popular
     */
    public static int getAmountOfPopularAndTopRated(Context context){
        int numMovies = context.getContentResolver().query(TMDbContract.Movies.URI,
                new String[]{TMDbContract.Movies.MOVIE_ID},
                TMDbContract.Movies.IS_POPULAR + " = ? OR " + TMDbContract.Movies.IS_TOP_RATED + " = ? ", new String[]{"1","1"}, null).getCount();

        return  numMovies;
    }


    /**
     * makes the network call to fetch the data and the calls storeJsonMovies
     * @param type Flag for the try of movie to fetch.
     * @return Returns an array of ids of the movies that were inserted
     */
    public static int[] fetchMovies(int type, Context context) {
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
                    ids = storeJsonMovies(rawJSON.toString(), type, context);
                    break;
                case MOVIES_TOP_RATED:
                    ids = storeJsonMovies(rawJSON.toString(), type, context);
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
    private static int[] storeJsonMovies(String dataJSON, int type, Context context) throws JSONException {
        //TODO use ContentProviderClient instead of ContentResolver
        try {
            JSONObject moviesJSON = new JSONObject(dataJSON);
            JSONArray results = moviesJSON.getJSONArray("results");
            final int resultLength = results.length();
            ContentValues[] contentValues = new ContentValues[resultLength];
            int[] ids = new int[resultLength];
            int isPopular;
            int isTopRated;
            int isFavorite;

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
            Cursor cursor = getFavoriteIds(context);
            ContentResolver contentResolver = context.getContentResolver();

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

                int moveId = movie.getInt(TMDbContract.Movies.MOVIE_ID);
                contentValues[i].put(TMDbContract.Movies.MOVIE_ID, moveId);
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()){
                    int favoriteId = cursor.getInt(cursor.getColumnIndex(TMDbContract.Movies.MOVIE_ID));
                    if (favoriteId == moveId){
                        deleteFavorite(context, moveId);
                        isFavorite = 1;
                        contentValues[i].put(TMDbContract.Movies.IS_FAVORITE, isFavorite);
                        break;
                    }
                }

                //TODO read up on how to have the col initialized to 0 by defualt
                contentValues[i].put(TMDbContract.Movies.IS_POPULAR, isPopular);
                contentValues[i].put(TMDbContract.Movies.IS_TOP_RATED, isTopRated);

                ids[i] = movie.getInt(TMDbContract.Movies.MOVIE_ID);
            }

            int numInserted = contentResolver.bulkInsert(TMDbContract.Movies.URI, contentValues);

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

    public static void fetchDetails(int[] movieIds, Context context) {
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
            if (movieIds[i] == 0){
                rawJsonVideos[i] = "";
                rawJsonReviews[i] = "";
                continue;
            }
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
            storeJsonVideos(rawJsonVideos, context);
            storeJsonReviews(rawJsonReviews, context);
        } catch (JSONException j){
            j.printStackTrace();
            Log.e(TAG,"Error ", j);
        }

    }

    private static String fetch(Uri uri) {
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
    private static void storeJsonVideos(String[] jsonDataArray, Context context) throws JSONException {
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

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues[] contentValues = new ContentValues[contentValuesArrayList.size()];
        contentValues = contentValuesArrayList.toArray(contentValues);

        int numInserted = contentResolver.bulkInsert(TMDbContract.Videos.URI, contentValues);

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
    private static void storeJsonReviews(String[] jsonDataArray, Context context) throws JSONException {
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
                            movie.getString(TMDbContract.Reviews.REVIEW_CONTENT));
                    value.put(TMDbContract.Reviews.REVIEW_ID,
                            movie.getString(TMDbContract.Reviews.REVIEW_ID));
                    value.put(TMDbContract.Reviews.MOVIE_IDS, movieId);
                    contentValuesArrayList.add(value);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues[] contentValues = new ContentValues[contentValuesArrayList.size()];
        contentValues = contentValuesArrayList.toArray(contentValues);

        int numInserted = contentResolver.bulkInsert(TMDbContract.Reviews.URI, contentValues);

        if (numInserted != contentValues.length) {
            Log.e(TAG, "Not all of the result were inserted.\n Amount inserted: " + numInserted
                    + "\nAmount from server: " + contentValues.length);
        }
    } //storeJsonReviews

}
