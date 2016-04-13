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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Defines a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
public class TMDbSyncService extends Service {

    private static final String TAG = "popularmovies " + TMDbSyncService.class.getSimpleName();

    private static TMDbSyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    public TMDbSyncService() {
        Log.v(TAG, "constructed");
    }

    /*
    * Instantiate the sync adapter object.
    */
    @Override
    public void onCreate() {
        //super.onCreate();
        Log.i(TAG, "OnCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new TMDbSyncAdapter(getApplicationContext(), true);
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }

}
