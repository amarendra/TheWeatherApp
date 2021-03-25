package com.olrep.theweatherapp.utils;

import android.text.format.DateUtils;

public final class Utils {
    // directly gets the weather condition icon url as per owm contract
    public static String getIconUrl(String iconId) {
        return "http://openweathermap.org/img/wn/" + iconId + ".png";   // can add @Nx before . to get bigger size todo
    }

    // returns the last updated time diff as "4 hours ago"
    public static String lastUpdated(long lastUpdated) {
        return (DateUtils.getRelativeTimeSpanString(lastUpdated)).toString();
    }

    // removes " ago" from "4 hours ago"
    public static String lastUpdatedShort(long lastUpdated) {
        String value = lastUpdated(lastUpdated);
        String ago = " ago";

        if (value.contains(ago)) {
            return value.replace(ago, "");
        }

        return value;
    }
}
