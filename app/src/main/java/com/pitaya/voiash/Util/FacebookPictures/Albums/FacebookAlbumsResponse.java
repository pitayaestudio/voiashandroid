
package com.pitaya.voiash.Util.FacebookPictures.Albums;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookAlbumsResponse implements Parcelable {

    @SerializedName("albums")
    @Expose
    private FacebookAlbums albums;
    @SerializedName("error")
    @Expose
    private AlbumError error;

    public FacebookAlbums getAlbums() {
        return albums;
    }

    public void setAlbums(FacebookAlbums albums) {
        this.albums = albums;
    }

    public AlbumError getError() {
        return error;
    }

    public void setError(AlbumError error) {
        this.error = error;
    }


    protected FacebookAlbumsResponse(Parcel in) {
        albums = (FacebookAlbums) in.readValue(FacebookAlbums.class.getClassLoader());
        error = (AlbumError) in.readValue(AlbumError.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(albums);
        dest.writeValue(error);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FacebookAlbumsResponse> CREATOR = new Parcelable.Creator<FacebookAlbumsResponse>() {
        @Override
        public FacebookAlbumsResponse createFromParcel(Parcel in) {
            return new FacebookAlbumsResponse(in);
        }

        @Override
        public FacebookAlbumsResponse[] newArray(int size) {
            return new FacebookAlbumsResponse[size];
        }
    };
}