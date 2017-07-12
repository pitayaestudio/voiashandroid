package com.pitaya.voiash.Util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pitaya.voiash.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by rulo on 10/07/17.
 */

public class UI {
    private static final String TAG = "UI";

    public static void setProfilePicture(Context context, String pictureUrl, ImageView target) {
        Glide.with(context).load(TextUtils.isEmpty(pictureUrl) ? "" : pictureUrl).placeholder(R.drawable.ic_profile).bitmapTransform(new CropCircleTransformation(context))//.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(target);
    }

    public static void setProfilePictureSquare(Context context, String pictureUrl, ImageView target) {
        Log.wtf(TAG, pictureUrl.toString());
        Glide.with(context).load(TextUtils.isEmpty(pictureUrl) ? "" : pictureUrl).placeholder(R.drawable.ic_profile)//.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(target);
    }

    public static void setProfilePicture(Context context, Uri pictureUri, ImageView target) {
        try {
            Log.wtf(TAG, pictureUri.toString());
            Glide.with(context).load(pictureUri).placeholder(R.drawable.ic_profile).priority(Priority.IMMEDIATE).error(R.drawable.ic_profile).bitmapTransform(new CropCircleTransformation(context)).into(target);
        } catch (Exception x) {
            Log.wtf(TAG, x.getMessage());
            x.printStackTrace();
        }
    }


}
