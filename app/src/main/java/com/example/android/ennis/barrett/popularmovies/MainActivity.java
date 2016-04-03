package com.example.android.ennis.barrett.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.ennis.barrett.popularmovies.asynchronous.FetchTMDb;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

public class MainActivity extends AppCompatActivity implements MainFragment.ListCommunicator {
    private static final String TAG = "popularmovies " + MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize default values for shared preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        Toast.makeText(this,
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.sortby_key), "-1"),
                Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/" + TMDbContract.Movies.TABLE_NAME);


        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_fetch_popular:
                getContentResolver().delete(uriTMDb, TMDbContract.Movies.IS_POPULAR + " = ?", new String[]{"1"});
                new FetchTMDb(this).execute(FetchTMDb.MOVIES_POPULAR);
                break;
            case R.id.action_fetch_top_rated:
                getContentResolver().delete(uriTMDb, TMDbContract.Movies.IS_TOP_RATED + " = ?", new String[]{"1"});
                new FetchTMDb(this).execute(FetchTMDb.MOVIES_TOP_RATED);
                break;
            default:
                Log.e(TAG, "Unhandled onOptionsItemSelected case in switch statement");
                break; //Serves no purpose?
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClicked(long _id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity._ID, _id);
        startActivity(intent);
    }
}
