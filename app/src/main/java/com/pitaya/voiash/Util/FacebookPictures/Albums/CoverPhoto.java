
package com.pitaya.voiash.Util.FacebookPictures.Albums;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoverPhoto implements Parcelable {

    @SerializedName("created_time")
    @Expose
    private String createdTime;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    protected CoverPhoto(Parcel in) {
        createdTime = in.readString();
        id = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createdTime);
        dest.writeString(id);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CoverPhoto> CREATOR = new Parcelable.Creator<CoverPhoto>() {
        @Override
        public CoverPhoto createFromParcel(Parcel in) {
            return new CoverPhoto(in);
        }

        @Override
        public CoverPhoto[] newArray(int size) {
            return new CoverPhoto[size];
        }
    };
}