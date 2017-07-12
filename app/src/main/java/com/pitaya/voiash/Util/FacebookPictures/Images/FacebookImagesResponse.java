
package com.pitaya.voiash.Util.FacebookPictures.Images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FacebookImagesResponse {

    @SerializedName("data")
    @Expose
    private List<FacebookImage> data = null;
    @SerializedName("imageError")
    @Expose
    private ImageError imageError;

    public List<FacebookImage> getData() {
        return data;
    }

    public void setData(List<FacebookImage> data) {
        this.data = data;
    }

    public ImageError getImageError() {
        return imageError;
    }

    public void setImageError(ImageError imageError) {
        this.imageError = imageError;
    }

}
