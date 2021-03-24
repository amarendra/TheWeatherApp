package com.olrep.theweatherapp.ui.detail;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.olrep.theweatherapp.contracts.WeatherCallback;
import com.olrep.theweatherapp.datasources.WeatherRepository;
import com.olrep.theweatherapp.entity.WeatherData;

public class WeatherDetailViewModel extends AndroidViewModel {
    private final WeatherRepository repository;
    private final MutableLiveData<Pair<Boolean, WeatherData>> currentCityWeather;

    public WeatherDetailViewModel(@NonNull Application application) {
        super(application);
        currentCityWeather = new MutableLiveData<>();
        repository = WeatherRepository.getInstance(application);
    }

    public LiveData<Pair<Boolean, WeatherData>> getCurrentCityWeather() {
        return currentCityWeather;
    }

    public void fetchCurrentCityWeather(String cityName) {
        WeatherData currentWeather = repository.getCachedWeather(cityName);
        currentCityWeather.postValue(new Pair<>(true, currentWeather));
        getWeather(cityName);
    }

    public void getWeather(String cityName) {
        repository.getCityWeather(cityName, new WeatherCallback<WeatherData>() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                currentCityWeather.postValue(new Pair<>(true, weatherData));
            }

            @Override
            public void onError(Throwable t) {
                currentCityWeather.postValue(new Pair<>(false, currentCityWeather.getValue().second));
            }
        });
    }
}
