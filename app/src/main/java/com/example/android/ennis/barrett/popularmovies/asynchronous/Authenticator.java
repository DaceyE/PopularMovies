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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 *  Implements AbstractAccountAuthenticator and stubs out all of its methods
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "popularmovies " + Authenticator.class.getSimpleName();

    /**
     * Creates the Authenticator, and calls AbstractAccountAuthenticator(context)
     * @param context The current Context
     */
    public Authenticator(Context context) {
        super(context);
        Log.v(TAG, "constructed");
    }

    /**
     * stub method, Editing properties is not supported.
     * Any calls with throw an exception.
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {

        throw new UnsupportedOperationException();
    }

    /**
     * stub method, Ignore attempts to confirm credentials.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {

        Log.d(TAG, "addAccount");
        return null;
    }

    /**
     * stub method, Ignore attempts to confirm credentials.
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
            Bundle options) throws NetworkErrorException {

        Log.d(TAG, "confirmCredentials");
        return null;
    }

    /**
     * stub method, Getting an authentication token is not supported.
     * Any calls with throw an exception.
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle options) throws NetworkErrorException {

        throw new UnsupportedOperationException();
    }

    /**
     * stub method, Getting a label for the auth token is not supported.
     * Any calls with throw an exception.
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    /**
     * stub method, Updating user credentials is not supported.
     * Any calls with throw an exception.
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle options) throws NetworkErrorException {

        throw new UnsupportedOperationException();
    }

    /**
     * stub method, Checking features for the account is not supported.
     * Any calls with throw an exception.
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
            String[] features) throws NetworkErrorException {

        throw new UnsupportedOperationException();
    }

}
