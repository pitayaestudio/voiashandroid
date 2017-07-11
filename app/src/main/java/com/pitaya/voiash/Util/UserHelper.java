package com.pitaya.voiash.Util;

import com.pitaya.voiash.Core.VoiashUser;

/**
 * Created by rulo on 11/07/17.
 */

public class UserHelper {
    public static String getFullName(VoiashUser voiashUser) {
        return String.format("%s %s", voiashUser.getName(), voiashUser.getLastName());
    }
}
