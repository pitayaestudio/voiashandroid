package com.pitaya.voiash.UI.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pitaya.voiash.R;

public class PasswordRecoverActivity extends BaseActivity {
    TextInputLayout til_recover_email;
    EditText et_recover_email;
    Button btn_recover_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til_recover_email = (TextInputLayout) findViewById(R.id.til_recover_email);
        et_recover_email = (EditText) findViewById(R.id.et_recover_email);
        btn_recover_pass = (Button) findViewById(R.id.btn_recover_pass);
        btn_recover_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecoverEmail();
            }

            private void sendRecoverEmail() {
                til_recover_email.setErrorEnabled(false);
                til_recover_email.setError(null);
                if (TextUtils.isEmpty(et_recover_email.getText())) {
                    til_recover_email.setErrorEnabled(true);
                    til_recover_email.setError(getString(R.string.error_field_required));
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(et_recover_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showAlert(getString(R.string.app_name), getString(R.string.lbl_recover_sent), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                    }
                });
            }
        });

    }
}
