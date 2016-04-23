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
package com.example.android.ennis.barrett.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "popularmovies " + DetailFragment.class.getSimpleName();

    private long mID = -1;
    private static final String ID_KEY = "theIdkey";
    private View mRootView;

    @Override
    public void onStart() {
        super.onStart();

        if (mID != -1 && getView() != null){
            setDetails(mID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (savedInstanceState != null) {
            mID = savedInstanceState.getLong(ID_KEY);
        }

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ID_KEY, mID);
    }

    public void setID(long _id) {
        this.mID = _id;
    }

    /**
     * Sets all the views to related id.
     * @param _id The id of the movie
     */
    private void setDetails(long _id) {
        /*
         * get references
         */
        TextView title = (TextView) mRootView.findViewById(R.id.original_title);
        TextView overview = (TextView) mRootView.findViewById(R.id.overview);
        TextView date = (TextView) mRootView.findViewById(R.id.date);
        TextView voteAverage2 = (TextView) mRootView.findViewById(R.id.vote_average2);
        ImageView poster = (ImageView) mRootView.findViewById(R.id.poster);
        RatingBar voteAverage = (RatingBar) mRootView.findViewById(R.id.vote_average);
        LinearLayout videos = (LinearLayout) mRootView.findViewById(R.id.videos);
        LinearLayout reviews = (LinearLayout) mRootView.findViewById(R.id.reviews);
        CompoundButton isFavorite = (CompoundButton) mRootView.findViewById(R.id.favorite);

        ContentResolver contentResolver = getActivity().getContentResolver();

        /*
         * Queries the movies table
         */
        Cursor cursor = contentResolver.query
                (TMDbContract.Movies.URI, null, TMDbContract.Movies.ID + " = ?", new String[]{mID + ""}, null);
        cursor.moveToFirst();

        /*
         * sets most views to the movie
         */
        title.setText(cursor.getString(
                cursor.getColumnIndex(TMDbContract.Movies.ORIGINAL_TITLE)));

        overview.setText(cursor.getString(
                cursor.getColumnIndex(TMDbContract.Movies.OVERVIEW)));

        date.setText(cursor.getString(
                cursor.getColumnIndex(TMDbContract.Movies.RELEASE_DATE)));

        // Setups up the poster
        String posterURLString = "http://image.tmdb.org/t/p/w185/"
                + cursor.getString(cursor.getColumnIndex(TMDbContract.Movies.POSTER));
        Log.v(TAG, posterURLString);
        Picasso.with(getActivity()).load(posterURLString).into(poster);

        String bool = cursor.getString(cursor.getColumnIndex(TMDbContract.Movies.IS_FAVORITE));

        isFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues value = new ContentValues();
                String isFavorite = "0";
                if (((CompoundButton) v).isChecked()){
                    isFavorite = "1";
                }

                value.put(TMDbContract.Movies.IS_FAVORITE, isFavorite);
                int num = getActivity().getContentResolver().update(TMDbContract.Movies.URI, value, TMDbContract.Movies._ID + " = ?",
                        new String[]{Long.toString(mID)});
            }
        });

        //short circuit logic stops app from crashing..So don't reverse the expression
        if(bool != null && bool.equals("1")) {
            isFavorite.setChecked(true);
        } else {
            isFavorite.setChecked(false);
        }

        //Set up the RatingBar and the TextView with the rating
        float vote = cursor.getFloat(cursor.getColumnIndex(TMDbContract.Movies.VOTE_AVERAGE));
        voteAverage2.setText(vote + " / 10 ");
        vote /= 2;
        Log.v(TAG, vote + "");
        voteAverage.setRating(vote);
        Log.v(TAG, voteAverage.getRating() + "");

        /*
         * Set up the videos LinearLayout.
         * Queries the table and then creates TextViews to display the results
         */
        Cursor cursorVideos = contentResolver.query(TMDbContract.Videos.URI, null,
                TMDbContract.Videos.MOVIE_IDS + " = ?",
                new String[]{cursor.getString(cursor.getColumnIndex(TMDbContract.Movies.MOVIE_ID))},
                null);
        VideoCursorAdapter adapter = new VideoCursorAdapter(getActivity(),R.layout.video_card,cursorVideos);

        //loop to create TextViews to display the results
        for (int i = 0; i < adapter.getCount(); i++){
            View view = adapter.getView(i,null,null);
            view.setOnClickListener(this);
            videos.addView(view);
        }


        /*
         * Set up the reviews LinearLayout.
         * Queries the table and then creates TextViews to display the results
         */
        Cursor cursorReviews = contentResolver.query(TMDbContract.Reviews.URI, null,
                TMDbContract.Reviews.MOVIE_IDS + " = ?",
                new String[]{cursor.getString(cursor.getColumnIndex(TMDbContract.Movies.MOVIE_ID))},
                null);

        if (cursorReviews.getCount() == 0) {
            mRootView.findViewById(R.id.reviews_header).setVisibility(View.GONE);
        } else {
            ReviewCursorAdapter adapter2 = new ReviewCursorAdapter(getActivity(),R.layout.review_card,cursorReviews);
            for (int i = 0; i < adapter2.getCount(); i++) {
                View view = adapter2.getView(i, null, null);
                reviews.addView(view);
            }
        }
    } //End of setDetails

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String key = (String) v.getTag();
        Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT).show();
        //TODO use youtube APIs instead.
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
            startActivity(intent);
        }
    }
}// end of class
