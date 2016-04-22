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
package com.example.android.ennis.barrett.popularmovies.asynchronous;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;

import com.example.android.ennis.barrett.popularmovies.BuildConfig;
import com.example.android.ennis.barrett.popularmovies.R;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContentProvider;
import com.example.android.ennis.barrett.popularmovies.data.TMDbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class TMDbSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "popularmovies " + TMDbSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 72000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    /**
     * Set up the sync adapter
     */
    TMDbSyncAdapter(Context context, boolean bool) {
        super(context, bool);
        Log.v(TAG, "Adapter Constructed");
    }

    //TODO implement (Context context, boolean autoInitialize, boolean allowParallelSyncs) to allow compatibility with Android 3.0 (11) and later platform versions

    /*
    * Calls any data transfer code needed by the app.
    */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        //TMDbSyncUtil.prepareLooper();
        Log.i(TAG, "onPerformSync");


        //ArrayList<Integer> list = new ArrayList<>(40);
        final Context context = getContext();

        TMDbSyncUtil.deletePopularAndTopRated(context);

        final int[] ids = new int[40];
        int externalIndex = 0;
        int[] tempIds = TMDbSyncUtil.fetchMovies(TMDbSyncUtil.MOVIES_POPULAR, context);
        for (int id : tempIds){
            ids[externalIndex] = id;
            externalIndex++;
        }
        tempIds = TMDbSyncUtil.fetchMovies(TMDbSyncUtil.MOVIES_TOP_RATED, context);
        for (int id : tempIds){
            ids[externalIndex] = id;
            externalIndex++;
        }

        Looper.prepare();
        Log.d(TAG, "Started");
        new CountDownTimer(100000L, 10000L)
        {
            int x = 0;
            int y = 14;

            @Override
            public void onTick(long millisUntilFinished) {
                int[] array = Arrays.copyOfRange(ids, x, y);
                TMDbSyncUtil.fetchDetails(array, context);

                if (x >= 28){
                    Looper.myLooper().quit();
                }

                x += 14;
                y += 14;
            }

            @Override
            public void onFinish(){
                Log.d(TAG, x +"");
                Log.d(TAG, "executed onFinish");

                while (x <= 28) {
                    Log.e(TAG, "Loop in onFinish of ContDownTimer executed.  May overload server");
                    int[] array = Arrays.copyOfRange(ids, x, y);
                    TMDbSyncUtil.fetchDetails(array, context);
                    x += 13;
                    y += 13;
                }

                Looper.myLooper().quit();
            }
        }.start();
        Looper.loop();
        Log.d(TAG, "onPerformSync ended");

    }





    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.v(TAG, "configurePeriodicSync");
        Account account = getSyncAccount(context);
        String authority = "com.example.android.ennis.barrett.popularmovies.data";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Log.i(TAG, "syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                "com.example.android.ennis.barrett.popularmovies.data", bundle);
    }

    public static Account getSyncAccount(Context context) {
        Log.v(TAG, "getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),
                "com.example.android.ennis.barrett.popularmovies.asynchronous.Authenticator");

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.v(TAG, "addAccountExplicitly");
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.i(TAG, "onAccountCreated");

        TMDbSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount,
                "com.example.android.ennis.barrett.popularmovies.data", true);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(TAG, "initializeSyncAdapter");
        getSyncAccount(context);
    }

}
