package edu.uiuc.cs427app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// Taken from https://developer.android.com/training/sync-adapters/creating-authenticator

// Service to help authenticate user
public class AuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(getApplicationContext());
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}