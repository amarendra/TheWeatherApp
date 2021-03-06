package com.olrep.theweatherapp.datasources;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static WeatherRepository instance;

    private static final String TAG = Constants.TAG + "WR";
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
        // lazy load todo
    }

    // returns all the favourite cities' weather data
    public LiveData<List<WeatherData>> getFavourites() {
        return weatherDao.getFavourites();
    }

    /**
     * @param cityName city for which cached data is needed
     * @return returns the cached data if available
     */
    @Nullable
    public WeatherData getCachedWeather(String cityName) {
        return weatherDao.getCachedWeather(cityName);
    }

    // a hacky attempt to find cached data if there's one in db close enough because i can't really find cache based on lat/long exactly
    // but it sort of works
    @Nullable
    public WeatherData getCachedWeather(double lat, double lon) {
        List<WeatherData> all = weatherDao.getAll();
        WeatherData cloestWeather = null;
        float minDist = Float.MAX_VALUE;
        float[] results = new float[2];

        if (all != null) {
            for (WeatherData weatherData : all) {
                Location.distanceBetween(lat, lon, weatherData.lat, weatherData.lon, results);
                Log.d(TAG, "distance for city -  " + weatherData.city + " is " + results[0]);

                if (results[0] < minDist) {
                    minDist = results[0];
                    cloestWeather = weatherData;
                }
            }
        }

        Log.d(TAG, "minDist: " + minDist + " | closet weather so far found: " + cloestWeather);

        if (cloestWeather != null && minDist < 1000 * 50.0) {   // for a location within 50km we treat as a the same city/place for cached weather -- refinement: what if two location gets merged due to this (e.g. twin cities) todo
            return cloestWeather;
        } else {
            return null;
        }
    }

    /**
     * @param cityName city name
     * @param callback to send data back to view model
     */
    public void getCityWeather(@NonNull String cityName, @NonNull final WeatherCallback<WeatherData> callback) {
        Call<CurrentWeather> cityCall = weatherService.getCurrentWeather(cityName);
        cityCall.enqueue(networkCallback(callback));
    }

    /**
     * @param lat, long lat, long
     * @param callback to send data back to view model
     */
    public void getCityWeather(@NonNull String lat, @NonNull String lon, @NonNull final WeatherCallback<WeatherData> callback) {
        Call<CurrentWeather> latLongCall = weatherService.getCurrentWeather(lat, lon);
        latLongCall.enqueue(networkCallback(callback));
    }

    /**
     * this is a common method to return network callback for both city and lat/long based calls
     * @param callback callback for data sending back to view model
     * @return retrofit callback
     */
    private Callback<CurrentWeather> networkCallback(@NonNull final WeatherCallback<WeatherData> callback) {
        return new Callback<CurrentWeather>() {
            @Override
            public void onResponse(@NotNull Call<CurrentWeather> call, @NotNull Response<CurrentWeather> response) {
                Log.d(TAG, "onResponse - response: " + response + " | response body: " + response.body());

                if (response.code() == 200 && response.body() != null) {
                    WeatherData weatherData = new WeatherData(response.body());

                    if (weatherDao.exists(weatherData.city_id)) {
                        if (weatherDao.isFav(weatherData.city_id)) {
                            Log.d(TAG, "Was an existing weather data so preserving fav state");
                            weatherData.favourite = true;
                        }

                        int res = update(weatherData);
                        Log.d(TAG, "item exists, called update: " + res);
                    } else {
                        insert(weatherData);
                        Log.d(TAG, "item didn't exist, called insert");
                    }

                    callback.onSuccess(weatherData);
                } else {
                    Log.e(TAG, "Response code is not 20, or body is null");
                    callback.onError(new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<CurrentWeather> call, @NotNull Throwable t) {
                Log.e(TAG, "onResponse: " + Log.getStackTraceString(t));
                callback.onError(t);
            }
        };
    }

    /**
     * @param setFav        whether to set fav or remove fav
     * @param weatherData   data that has to be updated in db
     * @return              returns row count where data was updated
     */
    public int setFavState(boolean setFav, WeatherData weatherData) {
        weatherData.favourite = setFav;
        return weatherDao.update(weatherData);
    }

    // =============== TODO ============================
    // this can be used for db ops later - so that when the db is quite big (just in case) and
    // even allowing on main thread doesn't help us anymore we can use this
    // but ui flow and logic will have to tweaked to expect and adjust for potential delays

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
