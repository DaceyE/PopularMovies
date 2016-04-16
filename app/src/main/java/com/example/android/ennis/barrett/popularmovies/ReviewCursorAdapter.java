package com.example.android.ennis.barrett.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Barrett on 4/16/2016.
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

