//TODO I feel like I should be extending a cursor adapter and not base adapter

package com.example.android.ennis.barrett.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Barrett on 2/29/2016.
 */
public class GridAdapter extends BaseAdapter {
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
        //TODO read up on the implications of the implicit cast taking place here...That's actually not happening anymore, but still read up on it :)
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
        //TODO Create place holder images? Picasso has api to help with that
        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

        String posterURLString = "http://image.tmdb.org/t/p/w185/" + poster;
        Log.v(TAG, posterURLString);
        Picasso.with(mContext).load(posterURLString).into(imageView);

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