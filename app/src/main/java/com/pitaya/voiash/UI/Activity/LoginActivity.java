package com.pitaya.voiash.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.Log;

import org.json.JSONObject;

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
        loginButton.setReadPermissions(new String[]{"public_profile", "email"});
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v(TAG, response.toString());
                                handleFacebookAccessToken(loginResult.getAccessToken(), response);
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
                        hideProgressDialog();
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed");
                            showAlert(getString(R.string.error_title), getString(R.string.error_credentials));
                        } else {
                            preferencesHelper.putIsEmailProvider();
                            preferencesHelper.putHasConfirmedEmail(task.getResult().getUser().isEmailVerified());
                        }

                    }
                });
    }


    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }


    private void handleFacebookAccessToken(final AccessToken token, final GraphResponse response) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            final VoiashUser voiashUser = new VoiashUser();
                            voiashUser.setEmail(user.getEmail());
                            voiashUser.setProvider("facebook");
                            String[] name = user.getDisplayName().split(" ");
                            voiashUser.setName(name[0]);
                            if (name.length > 1)
                                voiashUser.setLastName(name[1]);
                            voiashUser.setProfilePicture("http://graph.facebook.com/" + token.getUserId() + "/picture?type=large");
                            getBaseReference().child("users").child(task.getResult().getUser().getUid()).setValue(voiashUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    facebookSingOut();
                                }

                                private void facebookSingOut() {
                                    LoginManager.getInstance().logOut();
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                        Log.d(TAG, String.format("%s %s (%s)", key,
                                value.toString(), value.getClass().getName()));
                    }
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential");
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, acct.getDisplayName());
                            String[] name = acct.getDisplayName().split(" ");
                            final VoiashUser voiashUser = new VoiashUser();
                            voiashUser.setEmail(acct.getEmail());
                            voiashUser.setProvider("google");
                            voiashUser.setName(name[0]);
                            if (name.length > 1)
                                voiashUser.setLastName(name[1]);
                            voiashUser.setProfilePicture(acct.getPhotoUrl().toString());
                            getBaseReference().child("users").child(task.getResult().getUser().getUid()).setValue(voiashUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    googleSignOut();
                                    Log.w(TAG, task.isSuccessful() + "");
                                    if (task.isSuccessful()) {
                                        Log.w(TAG, voiashUser.toString());
                                    } else {
                                        Log.w(TAG, task.getException().getMessage());
                                        task.getException().printStackTrace();
                                    }
                                }

                                private void googleSignOut() {
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
                            });
                        }
                    }
                });
    }

}

