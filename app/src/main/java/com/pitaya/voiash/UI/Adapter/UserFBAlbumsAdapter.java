package com.pitaya.voiash.UI.Adapter;

import android.app.Activity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pitaya.voiash.R;
import com.pitaya.voiash.UI.Interface.FBAlbumSelectedListener;
import com.pitaya.voiash.Util.FacebookPictures.Albums.FacebookAlbumData;
import com.pitaya.voiash.Util.PreferencesHelper;
import com.pitaya.voiash.Util.UI;

/**
 * Created by rulo on 12/07/17.
 */

public class UserFBAlbumsAdapter extends RecyclerView.Adapter<UserFBAlbumsAdapter.AlbumViewHolder> {
    private final LayoutInflater mInflater;
    private SortedList<FacebookAlbumData> albumsData;
    private Activity mContext;
    private PreferencesHelper preferencesHelper;
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private FBAlbumSelectedListener fbAlbumSelectedListener;

    public UserFBAlbumsAdapter(Activity context) {
        if (context instanceof FBAlbumSelectedListener)
            fbAlbumSelectedListener = (FBAlbumSelectedListener) context;
        this.mInflater = LayoutInflater.from(context);
        this.albumsData = new SortedList<>(FacebookAlbumData.class, new SortedList.Callback<FacebookAlbumData>() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public int compare(FacebookAlbumData o1, FacebookAlbumData o2) {
                return o1.getName().compareTo(o2.getName());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(FacebookAlbumData oldItem, FacebookAlbumData newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(FacebookAlbumData item1, FacebookAlbumData item2) {
                return item1.toString().equals(item2.toString());
            }
        });
        this.mContext = context;
        this.preferencesHelper = new PreferencesHelper(context);
    }


    public void addAlbum(FacebookAlbumData album) {
        albumsData.add(album);
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.user_album_item_layout, parent, false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.bind(albumsData.get(position));
    }

    @Override
    public int getItemCount() {
        return albumsData.size();
    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_fb_album;
        TextView txt_fb_album_name;
        LinearLayout album_container;
        FacebookAlbumData album;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            this.album_container = (LinearLayout) itemView.findViewById(R.id.album_container);
            this.img_fb_album = (ImageView) itemView.findViewById(R.id.img_fb_album);
            this.txt_fb_album_name = (TextView) itemView.findViewById(R.id.txt_fb_album_name);
        }

        public void bind(final FacebookAlbumData album) {
            this.album = album;
            UI.setProfilePictureSquare(mContext, String.format(ALBUM_PHOTO_FORMAT, album.getCoverPhoto().getId(), preferencesHelper.getFacebookToken()), img_fb_album);
            txt_fb_album_name.setText(album.getName());
            album_container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (fbAlbumSelectedListener != null)
                fbAlbumSelectedListener.onAlbumSelected(this.album);
        /*    Intent photoIntent = new Intent(mContext, FacebookPhotosActivity.class);
            photoIntent.putExtra(ALBUM_EXTRA, album);
            mContext.startActivityForResult(photoIntent, FACEBOOK_PHOTOS_REQUEST_CODE);*/
        }
    }
}