package com.pitaya.voiash.Util.FacebookPictures;

import com.google.gson.Gson;
import com.pitaya.voiash.Util.FacebookPictures.Albums.AlbumError;
import com.pitaya.voiash.Util.FacebookPictures.Albums.FacebookAlbumData;
import com.pitaya.voiash.Util.FacebookPictures.Albums.FacebookAlbumsResponse;
import com.pitaya.voiash.Util.FacebookPictures.Images.FacebookImage;
import com.pitaya.voiash.Util.FacebookPictures.Images.FacebookImagesResponse;
import com.pitaya.voiash.Util.FacebookPictures.Images.ImageError;
import com.pitaya.voiash.Util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rulo on 12/07/17.
 */
public class FacebookPictureHelper {
    public interface AlbumLoadListener {
        void onAlbumLoaded(FacebookAlbumData facebookAlbumData);

        void onAllAlbumsLoaded(Integer albumsCount);

        void onAlbumsException(Exception exception);

        void onFacebookError(AlbumError albumError);
    }

    public interface AlbumImagesLoadListener {
        void onImageLoaded(FacebookImage facebookImage);

        void onAllImagesLoaded(Integer imagesCount);

        void onImageException(Exception exception);

        void onFacebookError(ImageError albumError);
    }

    private static final String TAG = "FacebookPictureHelper";

    public void getAlbums(String accesToken, final AlbumLoadListener albumLoadListener) {
        Log.wtf(TAG, "getAlbums");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://graph.facebook.com/me?access_token=" + accesToken + "&fields=albums%7Bcover_photo%2Cname%2Ccreated_time%7D")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.wtf(TAG, responseString);
                    FacebookAlbumsResponse facebookAlbumsResponse = new Gson().fromJson(responseString, FacebookAlbumsResponse.class);
                    int count = 0;
                    if (facebookAlbumsResponse.getError() != null) {
                        if (albumLoadListener != null)
                            albumLoadListener.onFacebookError(facebookAlbumsResponse.getError());
                    } else {
                        for (FacebookAlbumData albumData : facebookAlbumsResponse.getAlbums().getData()) {
                            if (albumLoadListener != null) {
                                albumLoadListener.onAlbumLoaded(albumData);
                                count++;
                            }
                        }
                        if (albumLoadListener != null)
                            albumLoadListener.onAllAlbumsLoaded(count);
                    }
                }
            }
        });
    }

    public void getPhotos(String albumId, String accesToken, final AlbumImagesLoadListener imagesLoadListener) {
        Log.wtf(TAG, "getPhotos");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://graph.facebook.com/" + albumId + "/photos?access_token=" + accesToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.wtf(TAG, responseString);
                    FacebookImagesResponse facebookImagesResponse = new Gson().fromJson(responseString, FacebookImagesResponse.class);
                    int count = 0;
                    if (facebookImagesResponse.getImageError() != null) {
                        if (imagesLoadListener != null)
                            imagesLoadListener.onFacebookError(facebookImagesResponse.getImageError());
                    } else {
                        for (FacebookImage facebookImage : facebookImagesResponse.getData()) {
                            if (imagesLoadListener != null) {
                                imagesLoadListener.onImageLoaded(facebookImage);
                                count++;
                            }
                        }
                        if (imagesLoadListener != null)
                            imagesLoadListener.onAllImagesLoaded(count);
                    }
                }
            }
        });
    }
}

