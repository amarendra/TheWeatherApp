package com.olrep.theweatherapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.olrep.theweatherapp.entity.WeatherData;

import java.util.List;

/**
 * generic room dao
 * some specific queries are written as per business logic needs
 */

@Dao
public interface WeatherDao {
    @Insert
    void insert(WeatherData weather);

    @Update
    int update(WeatherData weather);

    @Delete
    void delete(WeatherData weather);

    // no use case as of now - it just returns all the weather data in the db
    @Query("SELECT * FROM weather_data ORDER BY last_updated DESC")
    List<WeatherData> getAll();

    // this is more of a todo item --> if at all i provide autocomplete or city suggestions
    // i can use this instead of calling an external apis that lists cities
    @Query("SELECT city from weather_data ORDER BY last_updated DESC")
    List<String> getCities();

    // all the favourite cities' weather data returned
    // last updated first
    @Query("SELECT * FROM weather_data WHERE favourite == 1 ORDER BY last_updated DESC")
    LiveData<List<WeatherData>> getFavourites();

    // if there's a city weather available we fetch it
    // would have been better if somehow i could implement this call based on city id but I guess as of now
    // i will just go with the assumption/hope that even for lat, long city bounds are properly managed on owm side
    @Query("SELECT * FROM weather_data WHERE city LIKE :cityName ORDER BY last_updated DESC limit 1")
    WeatherData getCachedWeather(String cityName);

    // checks whether the city data received from api call is already present in db
    // this needed to decide whether to call update or inset
    @Query("SELECT EXISTS(SELECT 1 FROM weather_data WHERE city_id = :cityId)")
    boolean exists(long cityId);

    // check whether there's this city with favourite true or not (in room boolean is treated as number 1 or 0)
    @Query("SELECT EXISTS(SELECT 1 FROM weather_data WHERE city_id = :cityId AND favourite == 1)")
    boolean isFav(long cityId);
}
