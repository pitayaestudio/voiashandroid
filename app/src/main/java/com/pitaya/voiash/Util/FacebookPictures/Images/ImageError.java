
package com.pitaya.voiash.Util.FacebookPictures.Images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageError {

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

}
