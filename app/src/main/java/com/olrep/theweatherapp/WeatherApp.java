package com.olrep.theweatherapp;

import android.app.Application;

import com.olrep.theweatherapp.datasources.WeatherRepository;

public class WeatherApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // since i am using allow on main thread in db ops (will try to implement proper bg threading later)
        // wanted the repo to cache the data quite early hence this
        WeatherRepository.getInstance(this);
    }
}
