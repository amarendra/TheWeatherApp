package com.olrep.theweatherapp.utils;

public final class Utils {
    public static String getIconUrl(String iconId) {
        return "http://openweathermap.org/img/wn/" + iconId + ".png";   // can add @Nx before . to get bigger size
    }
}
