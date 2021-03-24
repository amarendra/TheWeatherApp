package com.olrep.theweatherapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.olrep.theweatherapp.entity.WeatherData;

import java.util.List;

@Dao
public interface WeatherDao {
    @Insert
    void insert(WeatherData weather);

    @Update
    int update(WeatherData weather);

    @Delete
    void delete(WeatherData weather);

    @Query("SELECT * FROM weather_data ORDER BY last_updated DESC")
    List<WeatherData> getAll();

    @Query("SELECT city from weather_data ORDER BY last_updated DESC")
    List<String> getCities();


    @Query("SELECT * FROM weather_data WHERE favourite ORDER BY last_updated DESC")
    LiveData<List<WeatherData>> getFavourites();

    @Query("SELECT * FROM weather_data WHERE city ==:cityName ORDER BY last_updated DESC limit 1")
    WeatherData getCachedWeather(String cityName);

    @Query("SELECT EXISTS(SELECT 1 FROM weather_data WHERE city_id = :cityId)")
    boolean exists(long cityId);
}
