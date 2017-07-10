package com.pitaya.voiash.Util;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by rulo on 10/07/17.
 */

public class ImageSelectorHelper {
    public static void requestFromGallery(Activity activity, Integer requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
    }

   /* public static void onSelectFromGalleryResult(Context context, Intent data) {
        File f = null;
        String filePath = "";
        Uri selectedImage = null;
        if (data != null && data.getData() != null) {
            try {
                selectedImage = data.getData();
                Log.v("file", selectedImage.toString());
                String wholeID = DocumentsContract.getDocumentId(selectedImage);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                    Log.v("filePath", filePath);
                    f = new File(filePath);
                } else {
                }
                cursor.close();
                cropImage(Uri.fromFile(f));
            } catch (Exception x) {
                try {
                    f = new File(getRealPathFromURI(selectedImage));
                    cropImage(Uri.fromFile(f));
                } catch (Exception ex) {

                }
            }
        }
    }



    private static void cropImage(Uri selectedImageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.setDataAndType(selectedImageUri, "image*//*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/
}
