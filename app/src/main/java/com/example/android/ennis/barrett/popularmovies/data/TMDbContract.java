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

    /**
     * contract class specifically for thevideos table
     */
    public class Videos implements BaseColumns{
        public static final String TABLE_NAME = "thevideos";

        //Most col map to the TMDb JSON object. MOVIE_IDS does not due to a collision
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String KEY = "key";
        public static final String TYPE = "type";
        public static final String VIDEO_ID = "id";
        //TODO make COL_MOVIE_ID a foreign key
        public static final String MOVIE_IDS = "ids";

        public static final int VIDEO_TYPE_TRAILER = 0;
        public static final int VIDEO_TYPE_TEASER = 1;
        public static final int VIDEO_TYPE_OTHER = 2;
    }

    /**
     * contract class specifically for themreviews table
     */
    public class Reviews implements BaseColumns{
        public static final String TABLE_NAME = "thereviews";

        //Most col map to the TMDb JSON object. MOVIE_IDS does not due to a collision
        public static final String ID = "_id";
        public static final String AUTHOR = "author";
        public static final String REVIEW_CONTENT = "content";
        public static final String REVIEW_ID = "id";
        //TODO make COL_MOVIE_ID a foreign key
        public static final String MOVIE_IDS = "ids";
    }

}
