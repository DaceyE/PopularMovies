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
package com.example.android.ennis.barrett.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.ennis.barrett.popularmovies.R;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Barrett on 2/29/2016.
 */
public class GridAdapter extends BaseAdapter {
//TODO I should be extending a cursor adapter and not baseAdapter

    private static final String TAG = "popularmovies " + GridAdapter.class.getSimpleName();

    Cursor mCursor;
    Context mContext;

    public GridAdapter(Context context) {
        this(context, null);
    }

    public GridAdapter(Context context, Cursor cursor) {
        this.mCursor = cursor;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mCursor.moveToPosition(position) ? mCursor : null;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position)
                ? mCursor.getLong(mCursor.getColumnIndex(TMDbContract.Movies._ID))
                : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mCursor.moveToPosition(position);
        String poster = mCursor.getString(mCursor.getColumnIndex(TMDbContract.Movies.POSTER));

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.poster_item, parent, false);
        }

        //TODO consider using a uri and additionally relocation the path as opposed the current hardcoding
        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

        String posterURLString = "http://image.tmdb.org/t/p/w185/" + poster;
        Picasso.with(mContext).load(posterURLString).placeholder(R.mipmap.movie_placeholder).into(imageView);

        return convertView;
    }

    public void changeCursor(Cursor newCursor) {
        if (newCursor == mCursor) return;

        //TODO can I cut these two lines of code by just calling .close() on mCursor without checking if its null and then set it equal to newCursor
        Cursor oldCursor = mCursor;
        mCursor = newCursor;

        if (newCursor != null) notifyDataSetChanged();

        if (oldCursor != null) oldCursor.close();
    }

}