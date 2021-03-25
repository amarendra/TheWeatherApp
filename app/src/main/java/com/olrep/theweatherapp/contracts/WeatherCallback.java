package com.olrep.theweatherapp.contracts;

/**
 * a generic callback to support different types of result and request
 * Retrofit's callback is <R, R> so didn't help as I needed a different pojo back
 * in the views and view model
 * @param <R>
 */
public interface WeatherCallback<R> {
    void onSuccess(R response);

    void onError(Throwable t);
}
