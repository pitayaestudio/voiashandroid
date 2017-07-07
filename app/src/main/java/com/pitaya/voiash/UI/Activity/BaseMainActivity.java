package com.pitaya.voiash.UI.Activity;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by rulo on 05/07/17.
 */

public class BaseMainActivity extends BaseActivity implements FirebaseAuth.AuthStateListener {
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}
