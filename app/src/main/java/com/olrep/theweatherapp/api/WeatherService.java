package com.olrep.theweatherapp.api;

import com.olrep.theweatherapp.model.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<CurrentWeather> getCurrentWeather(@Query("q") String city);

    Call<CurrentWeather> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon);
}
