package com.olrep.theweatherapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.olrep.theweatherapp.model.CurrentWeather;

@Entity(tableName = "weather_data")
public class WeatherData {
    @PrimaryKey
    public int city_id;

    @ColumnInfo(name = "city")
    public String city;

    public double lat;
    public double lon;

    public String weather_icon;
    public String weather_description;

    public double temp;
    public double temp_min;
    public double temp_max;
    public double temp_feels_like;

    public double wind_speed;

    public long dt;

    public String country;   // country code

    // not part of the weather api data
    public long last_updated;
    public short favourite = -1; // -1: unset; 0: not fav; 1: fav

    public WeatherData() {
    }

    public WeatherData(CurrentWeather cw) {
        this.city_id = cw.getId();
        this.city = cw.getName();

        if (cw.getCoord() != null) {
            this.lat = cw.getCoord().getLat();
            this.lon = cw.getCoord().getLon();
        }

        if (cw.getWeather() != null && cw.getWeather().get(0) != null) {
            this.weather_icon = cw.getWeather().get(0).getIcon();
            this.weather_description = cw.getWeather().get(0).getDescription();
        }

        if (cw.getMain() != null) {
            this.temp = cw.getMain().getTemp();
            this.temp_min = cw.getMain().getTempMin();
            this.temp_max = cw.getMain().getTempMax();
            this.temp_feels_like = cw.getMain().getFeelsLike();
        }

        if (cw.getWind() != null) {
            this.wind_speed = cw.getWind().getSpeed();
        }

        this.dt = cw.getDt();

        if (cw.getSys() != null) {
            this.country = cw.getSys().getCountry();
        }

        updateTime();
    }

    public void updateTime() {
        this.last_updated = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "city_id=" + city_id +
                ", city='" + city + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", weather_icon='" + weather_icon + '\'' +
                ", weather_description='" + weather_description + '\'' +
                ", temp=" + temp +
                ", temp_min=" + temp_min +
                ", temp_max=" + temp_max +
                ", temp_feels_like=" + temp_feels_like +
                ", wind_speed=" + wind_speed +
                ", dt=" + dt +
                ", country='" + country + '\'' +
                ", last_updated=" + last_updated +
                ", favourite=" + favourite +
                '}';
    }
}
