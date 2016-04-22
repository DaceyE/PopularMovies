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
public class VideoCursorAdapter extends ResourceCursorAdapter {

    public VideoCursorAdapter(Context context, int layout, Cursor cursor){
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
        ImageView imageView = (ImageView) view.findViewById(R.id.video_thumbnail);
        TextView textView = (TextView) view.findViewById(R.id.video_title);

        String name = cursor.getString(cursor.getColumnIndex(TMDbContract.Videos.NAME));
        textView.setText(name);

        String key = cursor.getString(cursor.getColumnIndex(TMDbContract.Videos.KEY));
        String url = "http://img.youtube.com/vi/" + key + "/0.jpg";

        Picasso.with(context).load(url).placeholder(R.mipmap.video_placeholder).into(imageView);

        //Associates the youtube key with each view
        view.setTag(key);
    }
}
