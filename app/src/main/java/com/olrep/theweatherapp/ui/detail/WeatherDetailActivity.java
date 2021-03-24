package com.olrep.theweatherapp.ui.detail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.olrep.theweatherapp.R;

public class WeatherDetailActivity extends AppCompatActivity {
    private WeatherDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_detail_activity);
        String city = "blr";
        viewModel = new ViewModelProvider(this).get(WeatherDetailViewModel.class);

        viewModel.getCurrentCityWeather().observe(this, currentWeather -> {

        });

        viewModel.fetchCurrentCityWeather(city);
    }
}
