package com.olrep.theweatherapp.api;

import com.olrep.theweatherapp.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApi {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.interceptors().add(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request request = chain.request();

                    // added interceptor to set units and app id for every call
                    HttpUrl url = request.url().newBuilder()
                            .addQueryParameter("units", "metric")
                            .addQueryParameter("appid", BuildConfig.OWM_STAGE_API_KEY)
                            .build();

                    request = request.newBuilder().url(url).build();
                    return chain.proceed(request);
                }
            });

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl("http://api.openweathermap.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }
}
