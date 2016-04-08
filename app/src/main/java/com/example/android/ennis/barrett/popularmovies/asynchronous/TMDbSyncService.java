package com.example.android.ennis.barrett.popularmovies.asynchronous;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TMDbSyncService extends Service {

    private static final String TAG = "popularmovies " + TMDbSyncService.class.getSimpleName();
    private static TMDbSyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    public TMDbSyncService() {
        Log.v(TAG, "constructed");
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        Log.i(TAG, "OnCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new TMDbSyncAdapter(getApplicationContext(), true);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
