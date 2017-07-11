package com.pitaya.voiash.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.Log;

/**
 * Created by rulo on 05/07/17.
 */

public class BaseMainActivity extends BaseActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "BaseMainActivity";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void signOut() {
        Log.wtf(TAG, "Sign Out");
        showProgressDialog();
        preferencesHelper.clear();
        mAuth.signOut();
    }

    public void deleteAccount() {
        Log.wtf(TAG, "Delete Account");
        showProgressDialog();
        preferencesHelper.clear();
        getBaseReference().child("users").child(mAuth.getCurrentUser().getUid()).removeValue();
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(BaseMainActivity.this, getString(R.string.lbl_account_deleted), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.wtf(TAG, "onStart");
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.wtf(TAG, "onStop");
        mAuth.removeAuthStateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
