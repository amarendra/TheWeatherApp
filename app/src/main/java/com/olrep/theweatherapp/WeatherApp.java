package com.olrep.theweatherapp;

import android.app.Application;

import com.olrep.theweatherapp.datasources.WeatherRepository;

public class WeatherApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WeatherRepository.getInstance(this);
    }
}
