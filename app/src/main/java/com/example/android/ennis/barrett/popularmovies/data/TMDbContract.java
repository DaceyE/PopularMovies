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

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract class for the TMDbContentProvider
 */
public class TMDbContract {
    //TODO Include URIs

    public static Uri buildURI(Uri uri, long id){
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * contract class specifically for themmovies table
     */
    public class Movies implements BaseColumns{
        public static final String TABLE_NAME = "themovies";
        public static final String ID = "_id";

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
