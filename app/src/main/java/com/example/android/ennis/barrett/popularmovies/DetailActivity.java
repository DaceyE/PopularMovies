package com.example.android.ennis.barrett.popularmovies;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "popularmovies " + DetailActivity.class.getSimpleName();

    //TODO programmatically define name
    public static final String _ID = "com.example.android.ennis.barrett.popularmovies.DetailActivity._ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        long _id = (long) getIntent().getExtras().get(_ID);

        //TODO do this in the manifest using styles/themes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);


        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        detailFragment.setID(_id);
    }
}