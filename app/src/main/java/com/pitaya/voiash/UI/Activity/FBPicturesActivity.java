package com.pitaya.voiash.UI.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.pitaya.voiash.R;
import com.pitaya.voiash.UI.Adapter.UserFBPhotosAdapter;
import com.pitaya.voiash.UI.Interface.FBPhotoSelectedListener;
import com.pitaya.voiash.Util.FacebookPictures.Albums.FacebookAlbumData;
import com.pitaya.voiash.Util.FacebookPictures.FacebookPictureHelper;
import com.pitaya.voiash.Util.FacebookPictures.Images.FacebookImage;
import com.pitaya.voiash.Util.FacebookPictures.Images.ImageError;
import com.pitaya.voiash.Util.Log;

public class FBPicturesActivity extends BaseActivity implements FacebookPictureHelper.AlbumImagesLoadListener, FBPhotoSelectedListener {
    public static final int FACEBOOK_PICTURE_REQUEST = 460;
    public static final String FACEBOOK_ALBUM_DATA = "FACEBOOK_ALBUM_DATA_EXTRA";
    public static final String EXTRA_SELECTED_IMAGE = "SELECTED_IMAGE";
    private static final String TAG = "FBPicturesActivity";
    FacebookAlbumData albumData;
    UserFBPhotosAdapter userFBPhotosAdapter;
    RecyclerView recycler_facebook_pictures;
    ProgressBar progress_fb_pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbpictures);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        albumData = getIntent().getParcelableExtra(FACEBOOK_ALBUM_DATA);
        if (albumData != null) {
            getSupportActionBar().setTitle(albumData.getName());
            userFBPhotosAdapter = new UserFBPhotosAdapter(this);
            progress_fb_pictures = (ProgressBar) findViewById(R.id.progress_fb_pictures);
            recycler_facebook_pictures = (RecyclerView) findViewById(R.id.recycler_facebook_pictures);
            recycler_facebook_pictures.setLayoutManager(new GridLayoutManager(this, 3));
            recycler_facebook_pictures.setAdapter(userFBPhotosAdapter);
            getAlbumPhotos(albumData.getId());
        } else {
            finish();
        }
    }

    private void getAlbumPhotos(String albumId) {
        new FacebookPictureHelper().getPhotos(albumId, preferencesHelper.getFacebookToken(), this);
    }

    @Override
    public void onImageLoaded(final FacebookImage facebookImage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress_fb_pictures.isShown())
                    progress_fb_pictures.setVisibility(View.GONE);
                userFBPhotosAdapter.addPhoto(facebookImage);
            }
        });

    }

    @Override
    public void onAllImagesLoaded(Integer imagesCount) {

    }

    @Override
    public void onImageException(Exception exception) {
        finish();
    }

    @Override
    public void onFacebookError(ImageError albumError) {
        finish();
    }

    @Override
    public void onPhotoSelected(String photoUrl) {
        Log.wtf(TAG, photoUrl);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SELECTED_IMAGE, photoUrl);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
