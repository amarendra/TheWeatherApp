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
    private final MutableLiveData<Pair<Boolean, WeatherData>> currentCityWeather; // Boolean part is to indicate error or success of an op

    public WeatherDetailViewModel(@NonNull Application application) {
        super(application);
        currentCityWeather = new MutableLiveData<>();
        repository = WeatherRepository.getInstance(application);
    }

    // this just provides the live data that activity can observe on
    public LiveData<Pair<Boolean, WeatherData>> getCurrentCityWeather() {
        Log.d(TAG, "getCurrentCityWeather called for live data");
        return currentCityWeather;
    }

    // if there's cached data, load it
    // and make an api call as well because activity always will have either city or lat/long
    public void fetchCurrentCityWeather(String cityName, double lat, double lon) {
        WeatherData cachedWeather = null;

        if (!TextUtils.isEmpty(cityName)) {
            Log.d(TAG, "Calling cache with city");
            cachedWeather = repository.getCachedWeather(cityName);
        } else {
            Log.d(TAG, "Calling cache with lat/lon");
            cachedWeather = repository.getCachedWeather(lat, lon);
        }

        currentCityWeather.postValue(new Pair<>(true, cachedWeather));

        Log.d(TAG, "cachedWeather fetched: " + cachedWeather);

        if (!TextUtils.isEmpty(cityName)) {
            getWeather(cityName);
        } else {
            getWeather(lat, lon);
        }
    }

    // fetch weather from owm with city name
    public void getWeather(@NonNull String cityName) {
        Log.d(TAG, "getWeather called for city: " + cityName);
        repository.getCityWeather(cityName, weatherCallback());
    }

    // fetch weather from owm with lat/long
    public void getWeather(double lat, double lon) {
        Log.d(TAG, "getWeather called for lat: " + lat + ", lon: " + lon);
        repository.getCityWeather(String.valueOf(lat), String.valueOf(lon), weatherCallback());
    }

    // just returns a weather call back - separate method as it's being used twice
    // it just modifies the mutable live data so that change will be propagated to activity
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

    // changes favourite state in db and propagates the change to live data as well so that activity can get it
    public void setFavState(boolean setFav) {
        int res = repository.setFavState(setFav, currentCityWeather.getValue().second);
        Log.d(TAG, "Set fav done: " + res);

        // on propagate changes if a row was updated
        if (res == 1) {
            currentCityWeather.getValue().second.favourite = setFav;
            currentCityWeather.postValue(new Pair<>(true, currentCityWeather.getValue().second));
        } else {    // some db error should view should know this
            currentCityWeather.postValue(new Pair<>(false, currentCityWeather.getValue().second));
        }
    }
}
