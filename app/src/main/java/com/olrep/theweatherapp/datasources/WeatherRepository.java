package com.olrep.theweatherapp.datasources;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.olrep.theweatherapp.contracts.WeatherCallback;
import com.olrep.theweatherapp.api.WeatherApi;
import com.olrep.theweatherapp.api.WeatherService;
import com.olrep.theweatherapp.db.WeatherDao;
import com.olrep.theweatherapp.db.WeatherDatabase;
import com.olrep.theweatherapp.entity.WeatherData;
import com.olrep.theweatherapp.model.CurrentWeather;
import com.olrep.theweatherapp.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static WeatherRepository instance;

    private static final String TAG = Constants.TAG + WeatherRepository.class.getSimpleName();
    private final WeatherDao weatherDao;
    private final WeatherService weatherService;

    public static WeatherRepository getInstance(Application application) {
        if (instance == null) {
            synchronized (WeatherRepository.class) {
                if (instance == null) {
                    instance = new WeatherRepository(application);
                }
            }
        }

        return instance;
    }

    private WeatherRepository(Application application) {
        WeatherDatabase db = WeatherDatabase.getInstance(application);
        weatherDao = db.weatherDao();
        weatherService = WeatherApi.getClient().create(WeatherService.class);
    }

    public void init() {

    }

    public LiveData<List<WeatherData>> getFavourites() {
        return weatherDao.getFavourites();
    }

    @Nullable
    public WeatherData getCachedWeather(String cityName) {
        return weatherDao.getCachedWeather(cityName);
    }

    public void getCityWeather(String cityName, final WeatherCallback<WeatherData> callback) {
        Call<CurrentWeather> call = weatherService.getCurrentWeather(cityName);
        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(@NotNull Call<CurrentWeather> call, @NotNull Response<CurrentWeather> response) {
                Log.d(TAG, "onResponse - response: " + response + " | response body: " + response.body());

                if (response.code() == 200) {
                    WeatherData weatherData = new WeatherData(response.body());

                    if (weatherDao.exists(weatherData.city_id)) {
                        int res = update(weatherData);
                        Log.d(TAG, "item exists, called update: " + res);
                    } else {
                        insert(weatherData);
                        Log.d(TAG, "item didn't exist, called insert");
                    }

                    callback.onSuccess(weatherData);
                } else {
                    Log.e(TAG, "Response code is not 200");
                    callback.onError( new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<CurrentWeather> call, @NotNull Throwable t) {
                Log.e(TAG, "onResponse: " + Log.getStackTraceString(t));
                callback.onError(t);
            }
        });
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void insert(WeatherData weather) {
        weatherDao.insert(weather);
        //executor.execute(new Worker(weatherDao, weather, Operation.INSERT));
    }

    public int update(WeatherData weather) {
        return weatherDao.update(weather);
        //executor.execute(new Worker(weatherDao, weather, Operation.UPDATE));
    }

    public void delete(WeatherData weather) {
        executor.execute(new Worker(weatherDao, weather, Operation.DELETE));
    }

    public static class Worker implements Runnable {
        private final WeatherDao weatherDao;
        private final Operation operation;
        private final WeatherData weather;

        public Worker(WeatherDao weatherDao, WeatherData weather, Operation operation) {
            this.weatherDao = weatherDao;
            this.weather = weather;
            this.operation = operation;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    weatherDao.insert(weather);
                    break;

                case UPDATE:
                    weatherDao.update(weather);
                    break;

                case DELETE:
                    weatherDao.delete(weather);
                    break;

                default:
                    break;
            }
        }
    }
}
