package com.pitaya.voiash.Util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rulo on 10/07/17.
 */

public class PermissionsHelper {
    public enum PERMISSIONS_RESULT {
        PERMISSIONS_OK,
        PERMISSIONS_FAIL
    }

    public static boolean checkAndRequestPermissions(Activity activity, Integer requestCode, List<String> permissionsList) {
        HashMap<String, Integer> permissionsMap = new HashMap<>();
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissionsList) {
            permissionsMap.put(permission, ContextCompat.checkSelfPermission(activity, permission));
        }
        for (Map.Entry<String, Integer> entry : permissionsMap.entrySet()) {
            if (entry.getValue() != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(entry.getKey());
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
            return false;
        }
        return true;
    }

    public static PERMISSIONS_RESULT validateGrantedPermissions(String[] permissions, int[] grantResults, List<String> permissionsList) {
        Map<String, Integer> perms = new HashMap<>();
        for (int i = 0, len = permissions.length; i < len; i++) {
            perms.put(permissions[i], grantResults[i]);
        }
        for (String permission : permissionsList) {
            if (perms.get(permission) != PackageManager.PERMISSION_GRANTED)
                return PERMISSIONS_RESULT.PERMISSIONS_FAIL;
        }
        return PERMISSIONS_RESULT.PERMISSIONS_OK;
    }
}
