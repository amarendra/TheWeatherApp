package com.olrep.theweatherapp.ui.detail;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.olrep.theweatherapp.contracts.WeatherCallback;
import com.olrep.theweatherapp.datasources.WeatherRepository;
import com.olrep.theweatherapp.entity.WeatherData;
import com.olrep.theweatherapp.utils.Constants;

import java.util.logging.Logger;

public class WeatherDetailViewModel extends AndroidViewModel {
    private final String TAG = Constants.TAG + "WDVM";

    private final WeatherRepository repository;
    private final MutableLiveData<Pair<Boolean, WeatherData>> currentCityWeather;

    public WeatherDetailViewModel(@NonNull Application application) {
        super(application);
        currentCityWeather = new MutableLiveData<>();
        repository = WeatherRepository.getInstance(application);
    }

    public LiveData<Pair<Boolean, WeatherData>> getCurrentCityWeather() {
        Log.d(TAG, "getCurrentCityWeather called for live data");
        return currentCityWeather;
    }

    public void fetchCurrentCityWeather(String cityName, String lat, String lon) {
        WeatherData cachedWeather = repository.getCachedWeather(cityName);
        currentCityWeather.postValue(new Pair<>(true, cachedWeather));

        Log.d(TAG, "cachedWeather fetched: " + cachedWeather);

        if (!TextUtils.isEmpty(cityName)) {
            getWeather(cityName);
        } else {
            getWeather(lat, lon);
        }
    }

    public void getWeather(@NonNull String cityName) {
        Log.d(TAG, "getWeather called for city: " + cityName);
        repository.getCityWeather(cityName, weatherCallback());
    }

    public void getWeather(@NonNull String lat, @NonNull String lon) {
        Log.d(TAG, "getWeather called for lat: " + lat + ", lon: " + lon);
        repository.getCityWeather(lat, lon, weatherCallback());
    }

    private WeatherCallback<WeatherData> weatherCallback() {
        return new WeatherCallback<WeatherData>() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                Log.d(TAG, "success (posting on live data)");
                currentCityWeather.postValue(new Pair<>(true, weatherData));
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error received (posting on live data): " + t.getMessage());
                currentCityWeather.postValue(new Pair<>(false, currentCityWeather.getValue().second));
            }
        };
    }

    public void setFavState(boolean setFav) {
        int res = repository.setFavState(setFav, currentCityWeather.getValue().second);
        Log.d(TAG, "Set fav done: " + res);

        if (res > 0) {
            currentCityWeather.getValue().second.favourite = setFav;
            currentCityWeather.postValue(new Pair<>(true, currentCityWeather.getValue().second));
        } else {
            currentCityWeather.postValue(new Pair<>(false, currentCityWeather.getValue().second));
        }
    }
}
