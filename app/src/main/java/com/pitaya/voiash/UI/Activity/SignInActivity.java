package com.pitaya.voiash.UI.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignInActivity extends BaseAuthActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivity";
    TextInputLayout[] textInputLayouts;
    EditText[] editTexts;
    private SimpleDateFormat birthdayDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    private Calendar userDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_main);
        getSupportActionBar().setTitle("");
        textInputLayouts = new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.til_signin_name),
                (TextInputLayout) findViewById(R.id.til_signin_lastname),
                (TextInputLayout) findViewById(R.id.til_signin_bday),
                (TextInputLayout) findViewById(R.id.til_signin_email),
                (TextInputLayout) findViewById(R.id.til_signin_pass),
                (TextInputLayout) findViewById(R.id.til_singnin_confirm_pass)
        };
        editTexts = new EditText[]{
                (EditText) findViewById(R.id.et_signin_name),
                (EditText) findViewById(R.id.et_signin_lastname),
                (EditText) findViewById(R.id.et_signin_bday),
                (EditText) findViewById(R.id.et_signin_email),
                (EditText) findViewById(R.id.et_signin_pass),
                (EditText) findViewById(R.id.et_signin_confirm_pass)
        };
        editTexts[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(SignInActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            userDate.set(Calendar.YEAR, year);
                            userDate.set(Calendar.MONTH, month);
                            userDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            editTexts[2].setText(birthdayDateFormat.format(userDate.getTime()));
                        } catch (Exception x) {
                            Log.wtf(TAG, x.getMessage());
                            x.printStackTrace();
                        }
                    }
                },
                        userDate.get(Calendar.YEAR),
                        userDate.get(Calendar.MONTH),
                        userDate.get(Calendar.DAY_OF_MONTH)
                );

                Calendar maxCalendar = Calendar.getInstance();
                maxCalendar.add(Calendar.YEAR, -18);
                dpd.getDatePicker().setMaxDate(maxCalendar.getTime().getTime());
                Calendar minCalendar = Calendar.getInstance();
                minCalendar.add(Calendar.YEAR, -100);
                dpd.getDatePicker().setMinDate(minCalendar.getTime().getTime());
                dpd.show();
            }
        });
        findViewById(R.id.btn_signin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                attempSignIn();
        }
    }

    private void attempSignIn() {
        clearErrors();
        if (validateNonEmpty() && isValidEmail() && isValidPass()) {
            final String email = editTexts[3].getText().toString();
            String password = editTexts[4].getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                VoiashUser voiashUser = new VoiashUser();
                                voiashUser.setEmail(email);
                                voiashUser.setProvider("email");
                                voiashUser.setName(editTexts[0].getText().toString());
                                voiashUser.setLastName(editTexts[1].getText().toString());
                                voiashUser.setBirthday(firebaseDateFormat.format(userDate.getTime()));
                                getBaseReference().child("users").child(task.getResult().getUser().getUid()).setValue(voiashUser);
                                task.getResult().getUser().sendEmailVerification();
                                preferencesHelper.putIsEmailProvider();
                                preferencesHelper.putHasConfirmedEmail(task.getResult().getUser().isEmailVerified());
                            } else {
                                if (task.getException() != null) {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        showAlert(getString(R.string.error_title), getString(R.string.error_user_exists));
                                    } else {
                                        showAlert(getString(R.string.error_title), getString(R.string.error_generic));
                                    }
                                }
                                Log.d(TAG, task.getException().getMessage());
                                task.getException().printStackTrace();
                            }
                        }
                    });
        }
    }

    private boolean isValidPass() {
        if (editTexts[4].getText().toString().contains(" ")) {
            textInputLayouts[4].setErrorEnabled(true);
            textInputLayouts[4].setError(getString(R.string.error_password_spaces));
            return false;
        }
        if (editTexts[4].getText().toString().length() < 6) {
            textInputLayouts[4].setErrorEnabled(true);
            textInputLayouts[4].setError(getString(R.string.error_password_length));
            return false;
        }
        if (!editTexts[4].getText().toString().equals(editTexts[5].getText().toString())) {
            textInputLayouts[4].setErrorEnabled(true);
            textInputLayouts[4].setError(getString(R.string.error_password_match));
            return false;
        }
        return true;
    }

    private boolean isValidEmail() {
        boolean res = android.util.Patterns.EMAIL_ADDRESS.matcher(editTexts[3].getText()).matches();
        if (!res) {
            textInputLayouts[3].setErrorEnabled(true);
            textInputLayouts[3].setError(getString(R.string.error_invalid_email));
        }
        return res;
    }

    private boolean validateNonEmpty() {
        for (int i = 0; i < editTexts.length; i++) {
            if (TextUtils.isEmpty(editTexts[i].getText())) {
                textInputLayouts[i].setErrorEnabled(true);
                textInputLayouts[i].setError(getString(R.string.error_field_required));
                editTexts[i].requestFocus();
                return false;
            }
        }
        return true;
    }

    private void clearErrors() {
        for (TextInputLayout til : textInputLayouts) {
            til.setError(null);
            til.setErrorEnabled(false);
        }
    }


}
