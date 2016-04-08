package com.example.android.ennis.barrett.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;


public class MainFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "popularmovies " + MainFragment.class.getSimpleName();
    private GridAdapter mAdapter;
    private static final String LOADER_BUNDLE_KEY = "thebundlekeys";

    public interface ListCommunicator {
        public void onClicked(long _id);
    }

    //TODO read up on fragment's constructors
    //TODO what is the access for the default [no-args] constructor?
    //public MainFragment() {
    //}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView started");

        //TODO I feel like the root view from fragment_main should have been a GridView (as opposed to LinearLayout with Gridview child) and I should be able to use the rootView from inflation as the reference to gridview
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPreferences.getString(getString(R.string.sortby_key), "-1");
        Log.d(TAG, getString(R.string.sortby_key));
        Log.d(TAG, "sortBy value: " + sortBy);
        Bundle loaderBundle = new Bundle(1);


        if (sortBy.equals("0")) {
            loaderBundle.putString(LOADER_BUNDLE_KEY, TMDbContract.Movies.IS_POPULAR);
        } else if (sortBy.equals("1")) {
            loaderBundle.putString(LOADER_BUNDLE_KEY, TMDbContract.Movies.IS_TOP_RATED);
        } else {
            Log.e(TAG, "unrecognized value from shared preference");
            loaderBundle = null;
        }

        mAdapter = new GridAdapter(getActivity());
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, loaderBundle, this);


        Log.v(TAG, "onCreateView ended...returning rootView");
        return rootView;
    } //onCreate


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated executed");
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO use a more stable solution
        ((ListCommunicator) getActivity()).onClicked(id);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged");
        if (key.equals(getString(R.string.sortby_key))) {
            String sortBy = sharedPreferences.getString(getString(R.string.sortby_key), "-1");
            Log.d(TAG, "onSharedPreferenceChanged(). sortBy value: " + sortBy);
            Bundle loaderBundle = new Bundle(1);

            if (sortBy.equals("0")) {
                loaderBundle.putString(LOADER_BUNDLE_KEY, TMDbContract.Movies.IS_POPULAR);
            } else if (sortBy.equals("1")) {
                loaderBundle.putString(LOADER_BUNDLE_KEY, TMDbContract.Movies.IS_TOP_RATED);
            } else {
                Log.e(TAG, "unrecognized value from shared preference," + sortBy);
                loaderBundle = null;
            }
            getLoaderManager().restartLoader(0, loaderBundle, this);

        } else {
            Log.e(TAG, "onSharedPreferenceChanged: case not handled. Key value: " + key);
        }
    }

    //TODO upon the addition of favorite I'll need to include a sort order in the bundle
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "onCreateLoader executed");
        switch (id) {
            case 0:
                String whereClause = null;
                String[] whereArgs = null;
                if (args != null) {
                    whereClause = args.getString(LOADER_BUNDLE_KEY) + " = ?";
                    whereArgs = new String[]{"1"};
                }

                Uri uri = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/" + TMDbContract.Movies.TABLE_NAME);
                Log.v(TAG, "case 0 chosen \n" + uri.toString());
                return new CursorLoader(getActivity(),
                        uri,
                        null,       // projection (col to return)
                        whereClause,// where clause
                        whereArgs,  // where args (values to insert in the ?)
                        null);      // Sort order
            default:
                Log.v(TAG, "onCreateLoader , default case chosen");
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "onLoadFinished executed");
        mAdapter.changeCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "onLoaderReset executed");
        mAdapter.changeCursor(null);
    }
}//end of class
