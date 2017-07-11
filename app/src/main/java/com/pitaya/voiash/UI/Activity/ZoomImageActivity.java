package com.pitaya.voiash.UI.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.UI;
import com.pitaya.voiash.Util.UserHelper;

public class ZoomImageActivity extends BaseActivity {
    public static final String ZOOMABLE_USER = "ZOOMABLE_USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            VoiashUser user = getIntent().getParcelableExtra(ZOOMABLE_USER);
            UI.setProfilePictureSquare(this, user.getProfilePicture(), (ImageView) findViewById(R.id.img_zoomed));
            getSupportActionBar().setTitle(UserHelper.getFullName(user));
        } catch (Exception x) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}
