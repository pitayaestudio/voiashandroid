
package com.pitaya.voiash.Util.FacebookPictures.Albums;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookAlbumData implements Parcelable {

    @SerializedName("cover_photo")
    @Expose
    private CoverPhoto coverPhoto;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created_time")
    @Expose
    private String createdTime;
    @SerializedName("id")
    @Expose
    private String id;

    public CoverPhoto getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(CoverPhoto coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FacebookAlbumData{" +
                "coverPhoto=" + coverPhoto +
                ", name='" + name + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    protected FacebookAlbumData(Parcel in) {
        coverPhoto = (CoverPhoto) in.readValue(CoverPhoto.class.getClassLoader());
        name = in.readString();
        createdTime = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(coverPhoto);
        dest.writeString(name);
        dest.writeString(createdTime);
        dest.writeString(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FacebookAlbumData> CREATOR = new Parcelable.Creator<FacebookAlbumData>() {
        @Override
        public FacebookAlbumData createFromParcel(Parcel in) {
            return new FacebookAlbumData(in);
        }

        @Override
        public FacebookAlbumData[] newArray(int size) {
            return new FacebookAlbumData[size];
        }
    };
}