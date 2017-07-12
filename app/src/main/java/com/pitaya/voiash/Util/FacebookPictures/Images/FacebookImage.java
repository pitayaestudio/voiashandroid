
package com.pitaya.voiash.Util.FacebookPictures.Images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookImage {

    @SerializedName("created_time")
    @Expose
    private String createdTime;
    @SerializedName("id")
    @Expose
    private String id;

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

}
