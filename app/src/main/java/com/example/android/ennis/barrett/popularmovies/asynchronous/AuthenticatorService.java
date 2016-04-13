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
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class AuthenticatorService extends Service {

    private static final String TAG = "popularmovies " + AuthenticatorService.class.getSimpleName();

    private Authenticator mAuthenticator;

    public AuthenticatorService() {
        Log.v(TAG, "Constructed");
    }

    /**
     * Initializes mAuthenticator
     */
    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        //Do I need to call super.onCreate()?
        mAuthenticator = new Authenticator(this);
    }

    /*
    * When the system binds to this Service to make the RPC call
    * return the authenticator's IBinder.
    */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mAuthenticator.getIBinder();
    }

}
