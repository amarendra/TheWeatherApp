package com.olrep.theweatherapp.contracts;

import com.olrep.theweatherapp.entity.WeatherData;

public interface ClickListener {
    void onClick(final WeatherData currentWeather);
}
