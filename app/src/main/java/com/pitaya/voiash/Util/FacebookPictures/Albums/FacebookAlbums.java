
package com.pitaya.voiash.Util.FacebookPictures.Albums;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FacebookAlbums implements Parcelable {

    @SerializedName("data")
    @Expose
    private List<FacebookAlbumData> data = null;

    public List<FacebookAlbumData> getData() {
        return data;
    }

    public void setData(List<FacebookAlbumData> data) {
        this.data = data;
    }


    protected FacebookAlbums(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<FacebookAlbumData>();
            in.readList(data, FacebookAlbumData.class.getClassLoader());
        } else {
            data = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FacebookAlbums> CREATOR = new Parcelable.Creator<FacebookAlbums>() {
        @Override
        public FacebookAlbums createFromParcel(Parcel in) {
            return new FacebookAlbums(in);
        }

        @Override
        public FacebookAlbums[] newArray(int size) {
            return new FacebookAlbums[size];
        }
    };
}
