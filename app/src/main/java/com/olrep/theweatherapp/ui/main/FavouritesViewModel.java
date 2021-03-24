package com.olrep.theweatherapp.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.olrep.theweatherapp.datasources.WeatherRepository;
import com.olrep.theweatherapp.entity.WeatherData;

import java.util.List;

public class FavouritesViewModel extends AndroidViewModel {
    private WeatherRepository repository;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        repository = WeatherRepository.getInstance(application);
    }

    public LiveData<List<WeatherData>> getFavourites() {
        LiveData<List<WeatherData>> favourites = repository.getFavourites();
        return favourites != null ? favourites : new MutableLiveData<>();
    }
}