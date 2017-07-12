
package com.pitaya.voiash.Util.FacebookPictures.Albums;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlbumError implements Parcelable {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("fbtrace_id")
    @Expose
    private String fbtraceId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getFbtraceId() {
        return fbtraceId;
    }

    public void setFbtraceId(String fbtraceId) {
        this.fbtraceId = fbtraceId;
    }


    protected AlbumError(Parcel in) {
        message = in.readString();
        type = in.readString();
        code = in.readByte() == 0x00 ? null : in.readInt();
        fbtraceId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(type);
        if (code == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(code);
        }
        dest.writeString(fbtraceId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AlbumError> CREATOR = new Parcelable.Creator<AlbumError>() {
        @Override
        public AlbumError createFromParcel(Parcel in) {
            return new AlbumError(in);
        }

        @Override
        public AlbumError[] newArray(int size) {
            return new AlbumError[size];
        }
    };
}