package com.example.android.ennis.barrett.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Barrett on 3/16/2016.
 */
public class TMDbContract {
    //TODO Include URIs

    public static Uri buildURI(Uri uri, long id){
        return ContentUris.withAppendedId(uri, id);
    }

    public class Movies implements BaseColumns{
        public static final String TABLE_NAME = "themovies";
        public static final String ID = "_id";
        //TODO ensure I'm using BaseColumns interface correctly...pretty sure I'm not..

        //Maps to the movies API JSON names
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER = "poster_path";
        public static final String RELEASE_DATE = "release_date";
        public static final String POPULARITY = "popularity";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_ID = "id";

        public static final String IS_POPULAR = "ispopular";
        public static final String IS_TOP_RATED = "istoprated";
        public static final String IS_FAVORITE = "isfavorite";
    }

}
