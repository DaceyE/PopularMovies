package com.example.android.ennis.barrett.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

import com.squareup.picasso.Picasso;

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
public class DetailFragment extends Fragment {

    private static final String TAG = "popularmovies " + DetailFragment.class.getSimpleName();

    private long mID = -1;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return mRootView;
    }

    public void setID(long _id) {
        this.mID = _id;
        setDetails(mID);
    }

    private void setDetails(long _id) {
        TextView title = (TextView) mRootView.findViewById(R.id.original_title);
        TextView overview = (TextView) mRootView.findViewById(R.id.overview);
        TextView date = (TextView) mRootView.findViewById(R.id.date);
        TextView voteAverage2 = (TextView) mRootView.findViewById(R.id.vote_average2);
        ImageView poster = (ImageView) mRootView.findViewById(R.id.poster);
        RatingBar voteAverage = (RatingBar) mRootView.findViewById(R.id.vote_average);

        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/"
                + TMDbContract.Movies.TABLE_NAME);
        Cursor cursor = getActivity().getContentResolver()
                .query(uriTMDb, null, TMDbContract.Movies.ID + " = ?", new String[]{mID + ""}, null);

        cursor.moveToFirst();

        title.setText(cursor.getString(
                cursor.getColumnIndex(TMDbContract.Movies.ORIGINAL_TITLE)));

        overview.setText(cursor.getString(
                cursor.getColumnIndex(TMDbContract.Movies.OVERVIEW)));

        date.setText(cursor.getString(
                cursor.getColumnIndex(TMDbContract.Movies.RELEASE_DATE)));

        String posterURLString = "http://image.tmdb.org/t/p/w185/"
                + cursor.getString(cursor.getColumnIndex(TMDbContract.Movies.POSTER));
        Log.v("dacey", posterURLString);
        Picasso.with(getActivity()).load(posterURLString).into(poster);




        float vote = cursor.getFloat(cursor.getColumnIndex(TMDbContract.Movies.VOTE_AVERAGE));
        voteAverage2.setText(vote + " / 10");

        vote /= 2;
        Log.v(TAG, vote + "");
        voteAverage.setRating(vote);
        Log.v(TAG, voteAverage.getRating() + "");
    }

}// end of class
