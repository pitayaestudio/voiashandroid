package com.pitaya.voiash.UI.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pitaya.voiash.R;
import com.pitaya.voiash.UI.Interface.FBPhotoSelectedListener;
import com.pitaya.voiash.Util.FacebookPictures.Images.FacebookImage;
import com.pitaya.voiash.Util.PreferencesHelper;
import com.pitaya.voiash.Util.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rulo on 12/07/17.
 */

public class UserFBPhotosAdapter extends RecyclerView.Adapter<UserFBPhotosAdapter.PhotoViewHolder> {
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private final LayoutInflater mInflater;
    private List<FacebookImage> photos;
    private Context mContext;
    private PreferencesHelper preferencesHelper;
    FBPhotoSelectedListener photoSelectedListener;

    public UserFBPhotosAdapter(Context context) {
        if (context instanceof FBPhotoSelectedListener)
            this.photoSelectedListener = (FBPhotoSelectedListener) context;
        this.mInflater = LayoutInflater.from(context);
        this.photos = new ArrayList<>();
        this.mContext = context;
        this.preferencesHelper = new PreferencesHelper(context);
    }


    public void addPhoto(FacebookImage photo) {
        photos.add(photos.size(), photo);
        notifyItemInserted(photos.size());
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.user_picture_fb_item_layout, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_photo_fb_item;
        View itemView;
        String photoUrl;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.img_photo_fb_item = (ImageView) itemView.findViewById(R.id.img_photo_fb_item);
        }

        public void bind(final FacebookImage facebookImage) {
            photoUrl = String.format(ALBUM_PHOTO_FORMAT, facebookImage.getId(), preferencesHelper.getFacebookToken(), preferencesHelper.getFacebookToken());
            UI.setProfilePictureSquare(mContext, photoUrl, img_photo_fb_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (photoSelectedListener != null)
                photoSelectedListener.onPhotoSelected(photoUrl);
        }
    }
}
