package com.example.android.ennis.barrett.popularmovies.asynchronous;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticatorService extends Service {

    private static final String TAG = "popularmovies " + AuthenticatorService.class.getSimpleName();

    private Authenticator mAuthenticator;

    public AuthenticatorService() {
        Log.v(TAG, "Constructed");
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        //Do I need to call super.onCreate()?
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mAuthenticator.getIBinder();
    }
}
