package com.olrep.theweatherapp.api;

import com.olrep.theweatherapp.model.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * both the methods of this api return the same response, just that once accepts city and another lat, long
 * if a location is inside a city --> then even in lat, long version of api call the city id and name (city name)
 * are the same
 */
public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<CurrentWeather> getCurrentWeather(@Query("q") String city);

    @GET("data/2.5/weather?")
    Call<CurrentWeather> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon);
}
