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

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.ennis.barrett.popularmovies.asynchronous.TMDbSyncAdapter;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

public class MainActivity extends AppCompatActivity implements MainFragment.ListCommunicator {
    private static final String TAG = "popularmovies " + MainActivity.class.getSimpleName();

    Account mAccount;
    private SharedPreferences.OnSharedPreferenceChangeListener SharedPreferenceChangeListener;

    public static final String AUTHORITY = "com.example.android.ennis.barrett.popularmovies.data";
    public static final String ACCOUNT_TYPE =
            "com.example.android.ennis.barrett.popularmovies.asynchronous.Authenticator";
    public static final String ACCOUNT = "mydummyaccount";

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        SharedPreferenceChangeListener = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_fragment);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(SharedPreferenceChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate started");

        //create dummy account for sync-adapter
        //mAccount = createSyncAccount(this);

        //Initialize default values for shared preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        Toast.makeText(this,
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.sortby_key), "-1"),
                Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/"
                + TMDbContract.Movies.TABLE_NAME);


        TMDbSyncAdapter.initializeSyncAdapter(this);

        //Calls an a manual sync if the provider is empty
        syncImmediately();

        Log.i(TAG, "onCreate finished");
    }

    /**
     * Calls for an expedited manual sync whenever the ContentProvider has no Movies flagged as popular.
     * Cannot be called before TMDbSyncAdapter.initializeSyncAdapter(Context)
     */
    private void syncImmediately(){
        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/"
                + TMDbContract.Movies.TABLE_NAME);

        int numMovies = getContentResolver().query(uriTMDb,
                new String[]{TMDbContract.Movies.MOVIE_ID},
                TMDbContract.Movies.IS_POPULAR + " = ?", new String[]{"1"}, null).getCount();

        if(numMovies <= 0){
            TMDbSyncAdapter.syncImmediately(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(SharedPreferenceChangeListener);
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

        Uri uriTMDb = Uri.parse("content://" + TMDbContentProvider.AUTHORITY + "/"
                + TMDbContract.Movies.TABLE_NAME);


        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_sort_popular:
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString(getString(R.string.sortby_key), "0").apply();

                break;
            case R.id.action_sort_top_rated:
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString(getString(R.string.sortby_key), "1").apply();
                break;
            default:
                Log.e(TAG, "Unhandled onOptionsItemSelected case in switch statement");
                break; //Serves no purpose?
        }


        return super.onOptionsItemSelected(item);
    }

    //MainFragment.ListCommunicator
    @Override
    public void onClicked(long _id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity._ID, _id);
        startActivity(intent);
    }

}
