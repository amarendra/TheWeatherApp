package com.olrep.theweatherapp.contracts;

public interface WeatherCallback<R> {
    void onSuccess(R response);

    void onError(Throwable t);
}
