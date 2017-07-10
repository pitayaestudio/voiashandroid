package com.pitaya.voiash.Util;

import android.widget.TextView;

import com.drivemode.android.typeface.TypefaceHelper;

/**
 * Created by rulo on 05/07/17.
 */

public class FontHelper {

    public static enum VoiashTypeface {
        GOTHAM_BLACK("Gotham-Black.otf"),
        GOTHAM_BOLD("Gotham-Bold.otf"),
        GOTHAM_LIGTH("Gotham-Light.otf"),
        GOTHAM_MEDIUM("Gotham-Medium.otf"),
        GOTHAM_ULTRA("Gotham-Ultra.otf");

        private String fontName;

        private VoiashTypeface(String toString) {
            fontName = toString;
        }

        @Override
        public String toString() {
            return fontName;
        }
    }

    public static <V extends TextView> void setTypefaceToViews(VoiashTypeface typeface, V... views) {
        for (V v : views) {
            TypefaceHelper.getInstance().setTypeface(v, typeface.fontName);
        }
    }
}
