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
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.example.android.ennis.barrett.popularmovies.R;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;
import com.squareup.picasso.Picasso;

/**
 * Displays Review cards
 */
public class ReviewCursorAdapter extends ResourceCursorAdapter {
    public ReviewCursorAdapter(Context context, int layout, Cursor cursor){
        super(context, layout, cursor, true);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.author);
        TextView textView1 = (TextView) view.findViewById(R.id.content);

        String author = cursor.getString(cursor.getColumnIndex(TMDbContract.Reviews.AUTHOR));
        textView.setText(author);

        String content = cursor.getString(cursor.getColumnIndex(TMDbContract.Reviews.REVIEW_CONTENT));
        textView1.setText(content);
    }
}

