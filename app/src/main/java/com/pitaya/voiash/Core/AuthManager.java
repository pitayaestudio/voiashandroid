package com.pitaya.voiash.Core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.pitaya.voiash.Util.Log;

/**
 * Created by rulo on 10/07/17.
 */

public class AuthManager {

    private static final String TAG = "AuthManager";

    public static void facebookSignOut() {
        LoginManager.getInstance().logOut();
    }

    public AccessToken getAccesToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public static void googleSignOut(final GoogleApiClient mGoogleApiClient) {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "Google Logged out");
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
    }
}
