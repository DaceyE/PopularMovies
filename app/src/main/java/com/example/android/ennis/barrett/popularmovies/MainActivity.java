package com.example.android.ennis.barrett.popularmovies;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    public static final String ACCOUNT_TYPE = "com.example.android.ennis.barrett.popularmovies.asynchronous.Authenticator";
    public static final String ACCOUNT = "mydummyaccount";

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        SharedPreferenceChangeListener = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(SharedPreferenceChangeListener);
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

        TMDbSyncAdapter.initializeSyncAdapter(this);
        Log.i(TAG, "onCreate finished");
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(SharedPreferenceChangeListener);
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
            case R.id.action_sort_popular:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(getString(R.string.sortby_key), "0").apply();
                //getContentResolver().delete(uriTMDb, TMDbContract.Movies.IS_POPULAR + " = ?", new String[]{"1"});
                //new FetchTMDb(this).execute(FetchTMDb.MOVIES_POPULAR);
                break;
            case R.id.action_sort_top_rated:
                //getContentResolver().delete(uriTMDb, TMDbContract.Movies.IS_TOP_RATED + " = ?", new String[]{"1"});
                //new FetchTMDb(this).execute(FetchTMDb.MOVIES_TOP_RATED);
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(getString(R.string.sortby_key), "1").apply();
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

    /*public static Account createSyncAccount(Context context){
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);

        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount,null,null)) Log.d(TAG, "new account created");
    }*/
}
