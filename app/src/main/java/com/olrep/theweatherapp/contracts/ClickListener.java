package com.olrep.theweatherapp.contracts;

import com.olrep.theweatherapp.entity.WeatherData;

/**
 * callback from adapter to fragment
 */
public interface ClickListener {
    void onClick(final WeatherData currentWeather);
}
