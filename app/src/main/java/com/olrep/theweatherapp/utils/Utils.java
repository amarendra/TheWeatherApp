package com.olrep.theweatherapp.utils;

import android.text.format.DateUtils;

public final class Utils {
    public static String getIconUrl(String iconId) {
        return "http://openweathermap.org/img/wn/" + iconId + ".png";   // can add @Nx before . to get bigger size
    }

    public static String lastUpdated(long lastUpdated) {
        return (DateUtils.getRelativeTimeSpanString(lastUpdated)).toString();
    }
}
