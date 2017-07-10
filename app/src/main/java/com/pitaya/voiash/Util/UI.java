package com.pitaya.voiash.Util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pitaya.voiash.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by rulo on 10/07/17.
 */

public class UI {
    public static void setProfilePicture(Context context, String pictureUrl, ImageView target) {
        Glide.with(context).load(TextUtils.isEmpty(pictureUrl) ? "" : pictureUrl).placeholder(R.drawable.ic_profile).bitmapTransform(new CropCircleTransformation(context)).diskCacheStrategy(DiskCacheStrategy.ALL).into(target);
    }


}
