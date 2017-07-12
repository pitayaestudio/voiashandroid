package com.pitaya.voiash.UI.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.pitaya.voiash.Core.AuthManager;
import com.pitaya.voiash.R;
import com.pitaya.voiash.UI.Adapter.UserFBAlbumsAdapter;
import com.pitaya.voiash.UI.Interface.FBAlbumSelectedListener;
import com.pitaya.voiash.Util.FacebookPictures.Albums.AlbumError;
import com.pitaya.voiash.Util.FacebookPictures.Albums.FacebookAlbumData;
import com.pitaya.voiash.Util.FacebookPictures.FacebookPictureHelper;
import com.pitaya.voiash.Util.Log;

import static com.pitaya.voiash.UI.Activity.FBPicturesActivity.EXTRA_SELECTED_IMAGE;
import static com.pitaya.voiash.UI.Activity.FBPicturesActivity.FACEBOOK_ALBUM_DATA;
import static com.pitaya.voiash.UI.Activity.FBPicturesActivity.FACEBOOK_PICTURE_REQUEST;

public class FBAlbumsActivity extends BaseMainActivity implements FacebookPictureHelper.AlbumLoadListener, FBAlbumSelectedListener {
    private RecyclerView recycler_facebook_albums;
    private UserFBAlbumsAdapter albumsAdapter;
    private LoginButton login_albums;
    private CallbackManager callbackManager;
    public static final int FACEBOOK_ALBUM_REQUEST = 954;
    private String TAG = "FBAlbumsActivity";
    private ProgressBar progress_fb_albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbalbums);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        login_albums = (LoginButton) findViewById(R.id.login_albums);
        albumsAdapter = new UserFBAlbumsAdapter(this);
        recycler_facebook_albums = (RecyclerView) findViewById(R.id.recycler_facebook_albums);
        progress_fb_albums = (ProgressBar) findViewById(R.id.progress_fb_albums);
        recycler_facebook_albums.setLayoutManager(new LinearLayoutManager(this));
        recycler_facebook_albums.setAdapter(albumsAdapter);
        if (!TextUtils.isEmpty(preferencesHelper.getFacebookToken())) {
            getUserAlbums();
        } else {
            facebookAuth();
        }
    }

    private void facebookAuth() {
        login_albums.setReadPermissions(new String[]{"public_profile", "email", "user_birthday", "user_photos"});
        callbackManager = CallbackManager.Factory.create();
        login_albums.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                preferencesHelper.putFacebookToken(loginResult.getAccessToken().getToken());
                getUserReference().child("fbToken").setValue(loginResult.getAccessToken().getToken());
                AuthManager.facebookSignOut();
                getUserAlbums();
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(FacebookException exception) {
                finish();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                login_albums.performClick();
            }
        });
    }


    private void getUserAlbums() {
        new FacebookPictureHelper().getAlbums(preferencesHelper.getFacebookToken(), this);
    }

    @Override
    public void onAlbumLoaded(final FacebookAlbumData facebookAlbumData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress_fb_albums.isShown())
                    progress_fb_albums.setVisibility(View.GONE);
                albumsAdapter.addAlbum(facebookAlbumData);
            }
        });

    }

    @Override
    public void onAllAlbumsLoaded(Integer albumsCount) {

    }

    @Override
    public void onAlbumsException(Exception exception) {
        finish();
    }

    @Override
    public void onFacebookError(AlbumError albumError) {
        facebookAuth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception x) {
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FACEBOOK_PICTURE_REQUEST:
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_SELECTED_IMAGE, data.getExtras().getString(EXTRA_SELECTED_IMAGE));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onAlbumSelected(FacebookAlbumData albumData) {
        Log.wtf(TAG, albumData.getId() + " " + albumData.getName());
        Intent pictureIntent = new Intent(this, FBPicturesActivity.class);
        pictureIntent.putExtra(FACEBOOK_ALBUM_DATA, albumData);
        startActivityForResult(pictureIntent, FACEBOOK_PICTURE_REQUEST);
    }
}
