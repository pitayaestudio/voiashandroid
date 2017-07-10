package com.pitaya.voiash.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.Log;

public class WelcomeActivity extends BaseAuthActivity implements View.OnClickListener {
    private static final String TAG = "WelcomeActivity";
    private Button btn_travel_deals, btn_login;
    private TranslateAnimation cloud_moving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btn_travel_deals = (Button) findViewById(R.id.btn_travel_deals);
        btn_login = (Button) findViewById(R.id.email_sign_in_button);
        btn_travel_deals.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    private void signInAnon() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously");
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_travel_deals:
                signInAnon();
                break;
            case R.id.email_sign_in_button:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
       /* cloud_moving = new TranslateAnimation(
                Animation.ABSOLUTE, 1450,
                Animation.ABSOLUTE, 10,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0
        );

        cloud_moving.setDuration(6000);
        cloud_moving.setFillAfter(false);
        cloud_moving.setStartOffset(1000);
        cloud_moving.setRepeatCount(Animation.INFINITE);
        cloud_moving.setRepeatMode(Animation.RESTART);
        findViewById(R.id.img_welcome_static).startAnimation(cloud_moving);*/
    }

    @Override
    public void onStop() {
        super.onStop();
      /*  cloud_moving.cancel();*/

    }
}
