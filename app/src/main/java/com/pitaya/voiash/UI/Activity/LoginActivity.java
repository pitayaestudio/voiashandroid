package com.pitaya.voiash.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pitaya.voiash.Core.AuthManager;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.Log;

import org.json.JSONObject;

import static com.pitaya.voiash.Core.AuthManager.googleSignOut;

public class LoginActivity extends BaseAuthActivity implements View.OnClickListener {
    private static final int RC_GOOGLE_SIGN_IN = 411;
    private static final String TAG = "LoginActivity";
    private Button email_sign_in_button;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextInputLayout til_email, til_pass;
    private EditText et_mail, et_pass;
    private TextView txt_password_recover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);
        findViewById(R.id.btn_login_fb).setOnClickListener(this);
        findViewById(R.id.btn_login_google).setOnClickListener(this);
        findViewById(R.id.btn_login_mail).setOnClickListener(this);
        email_sign_in_button.setOnClickListener(this);
        txt_password_recover = (TextView) findViewById(R.id.txt_password_recover);
        txt_password_recover.setOnClickListener(this);
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_pass = (TextInputLayout) findViewById(R.id.til_pass);
        et_mail = (EditText) findViewById(R.id.et_email);
        et_pass = (EditText) findViewById(R.id.et_pass);
        email_sign_in_button.setOnClickListener(this);
        loginButton.setReadPermissions(new String[]{"public_profile", "email", "user_birthday", "user_photos"});
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v(TAG, response.getJSONObject().toString());
                                handleFacebookAccessToken(loginResult.getAccessToken(), response.getJSONObject().optString("birthday", null));
                                showProgressDialog();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.v(TAG, "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v(TAG, exception.getCause().toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_fb:
                loginButton.performClick();
                break;
            case R.id.btn_login_google:
                loginWithGoogle();
                break;
            case R.id.btn_login_mail:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.email_sign_in_button:
                attempLogin();
                break;
            case R.id.txt_password_recover:
                startActivity(new Intent(this, PasswordRecoverActivity.class));
                break;
        }
    }

    private void attempLogin() {
        til_email.setErrorEnabled(false);
        til_pass.setErrorEnabled(false);
        til_email.setError(null);
        til_email.setError(null);
        if (TextUtils.isEmpty(et_mail.getText().toString())) {
            til_email.setErrorEnabled(true);
            til_email.setError(getString(R.string.error_field_required));
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_mail.getText().toString()).matches()) {
            til_email.setErrorEnabled(true);
            til_email.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (TextUtils.isEmpty(et_pass.getText().toString())) {
            til_pass.setErrorEnabled(true);
            til_pass.setError(getString(R.string.error_field_required));
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(et_mail.getText().toString(), et_pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            preferencesHelper.putIsEmailProvider();
                            preferencesHelper.putHasConfirmedEmail(task.getResult().getUser().isEmailVerified());
                        } else {
                            manageFirebaseError(task.getException());
                        }
                    }
                });
    }

    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void handleFacebookAccessToken(final AccessToken token, final String birthday) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = task.getResult().getUser();
                    final VoiashUser voiashUser = new VoiashUser();
                    voiashUser.setEmail(user.getEmail());
                    voiashUser.setProvider("facebook");
                    String[] name = user.getDisplayName().split(" ");
                    voiashUser.setName(name[0]);
                    voiashUser.setBirthday(birthday);
                    if (name.length > 1)
                        voiashUser.setLastName(name[1]);
                    voiashUser.setProfilePicture("http://graph.facebook.com/" + token.getUserId() + "/picture?type=large");
                    voiashUser.setFbToken(token.getToken());
                    preferencesHelper.putFacebookToken(token.getToken());
                    setUserInfo(voiashUser);
                } else {
                    AuthManager.facebookSignOut();
                    manageFirebaseError(task.getException());
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, acct.getDisplayName());
                            String[] name = acct.getDisplayName().split(" ");
                            final VoiashUser voiashUser = new VoiashUser();
                            voiashUser.setEmail(acct.getEmail());
                            voiashUser.setProvider("google");
                            voiashUser.setName(name[0]);
                            if (name.length > 1)
                                voiashUser.setLastName(name[1]);
                            voiashUser.setProfilePicture(acct.getPhotoUrl().toString());
                            googleSignOut(mGoogleApiClient);
                            setUserInfo(voiashUser);
                        } else {
                            manageFirebaseError(task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            showProgressDialog();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                hideProgressDialog();
            }
        }
    }

    private void manageFirebaseError(Exception exception) {
        hideProgressDialog();
        Log.wtf(TAG, exception.getMessage());
        exception.printStackTrace();
        if (exception instanceof FirebaseAuthUserCollisionException) {
            showAlert(getString(R.string.error_title), getString(R.string.lbl_collision_account));
        } else {
            showAlert(getString(R.string.error_title), getString(R.string.error_generic));
        }
    }

    private void setUserInfo(final VoiashUser user) {
        getUserReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VoiashUser thisUser = dataSnapshot.getValue(VoiashUser.class);
                if (thisUser == null) {
                    getUserReference().setValue(user);
                } else {
                    if (!TextUtils.isEmpty(thisUser.getFbToken())) {
                        preferencesHelper.putFacebookToken(thisUser.getFbToken());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}

