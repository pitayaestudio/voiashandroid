package com.pitaya.voiash.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fickz on 07/07/2017.
 */

public class PreferencesHelper {
    private final Context context;
    private SharedPreferences preferences;
    private final String EMAIL_PROVIDER = "EMAIL_PROVIDER";
    private final String HAS_CONFIRMED_EMAIL = "HAS_CONFIRMED_EMAIL";

    public PreferencesHelper(Context context) {
        preferences = context.getSharedPreferences("MUAPP_PREF", Context.MODE_PRIVATE);
        this.context = context;
    }

    public void clear() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }


    public void putIsEmailProvider() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(EMAIL_PROVIDER, true);
        edit.apply();
    }

    public boolean getIsEmailProvider() {
        return preferences.getBoolean(EMAIL_PROVIDER, false);
    }

    public void putHasConfirmedEmail(boolean confirmedEmail) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(HAS_CONFIRMED_EMAIL, confirmedEmail);
        edit.apply();
    }

    public boolean getHasConfirmedEmail() {
        return preferences.getBoolean(HAS_CONFIRMED_EMAIL, false);
    }

}
