package com.pitaya.voiash.Application;

import android.app.Application;

import com.drivemode.android.typeface.TypefaceHelper;

/**
 * Created by rulo on 05/07/17.
 */

public class VoiashApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceHelper.initialize(this);
    }

    @Override
    public void onTerminate() {
        TypefaceHelper.destroy();
        super.onTerminate();
    }
}
