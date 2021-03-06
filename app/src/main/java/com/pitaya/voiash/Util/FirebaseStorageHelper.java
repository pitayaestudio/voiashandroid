package com.pitaya.voiash.Util;

import android.net.Uri;
import android.support.v7.appcompat.BuildConfig;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by rulo on 11/07/17.
 */

public class FirebaseStorageHelper {
    public void uploadProfilePhoto(String userId, Uri photoUri, OnCompleteListener<UploadTask.TaskSnapshot> onCompleteListener) {
        StorageReference profileRef = FirebaseStorage.getInstance().getReference().child(BuildConfig.DEBUG ? "dev_users" : "users").child(userId).child("profile").child("profile_picture.jpg");
        profileRef.delete();
        uploadPhotos(profileRef, photoUri, onCompleteListener);
    }

    private void uploadPhotos(StorageReference reference, Uri uri, OnCompleteListener<UploadTask.TaskSnapshot> onCompleteListener) {
        reference.putFile(uri).addOnCompleteListener(onCompleteListener);
    }

    public void uploadProfilePhoto(String userId, byte[] imgArray, OnCompleteListener<UploadTask.TaskSnapshot> onCompleteListener) {
        StorageReference profileRef = FirebaseStorage.getInstance().getReference().child(BuildConfig.DEBUG ? "dev_users" : "users").child(userId).child("profile").child("profile_picture.jpg");
        profileRef.delete();
        uploadPhotos(profileRef, imgArray, onCompleteListener);
    }

    private void uploadPhotos(StorageReference reference, byte[] imgArray, OnCompleteListener<UploadTask.TaskSnapshot> onCompleteListener) {
        reference.putBytes(imgArray).addOnCompleteListener(onCompleteListener);
    }
}
