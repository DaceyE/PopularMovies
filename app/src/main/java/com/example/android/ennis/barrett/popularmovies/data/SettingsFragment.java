package com.example.android.ennis.barrett.popularmovies.data;

import android.os.Bundle;
import android.preference.PreferenceFragment;

//TODO read about this import
import com.example.android.ennis.barrett.popularmovies.R;

/**
 * Created by Barrett on 3/24/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
