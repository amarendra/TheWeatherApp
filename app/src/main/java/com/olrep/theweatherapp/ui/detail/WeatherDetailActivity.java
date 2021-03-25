package com.olrep.theweatherapp.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.utils.Constants;
import com.olrep.theweatherapp.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * this activity shows the full screen view of a city/place weather
 * this can be made prettier and more functional but for a poc this is where I am
 * leaving it as of now
 *
 * todo
 *  add owm's onecall api for this screen to show a collapsible weather forecast along with current weather data
 *  that will help me avoid make two api calls but onecall api response will be too heavy
 */
public class WeatherDetailActivity extends AppCompatActivity {
    private final String TAG = Constants.TAG + "WDA";

    // these are to be used in floating button to indicate whether as of now place/city
    // is a favourite or not
    private final String FAV_TAG = "is_fav";
    private final String NOT_FAV_TAG = "is_not_fav";

    private WeatherDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_detail_activity);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(WeatherDetailViewModel.class);

        Intent intent = getIntent();

        String city = null;
        double lat = 0.0, lon = 0.0;

        // since i have to get city and lat/long both from last screen
        // i decided to save both and handle with one method call
        if (intent.hasExtra("city")) {
            city = intent.getStringExtra("city");
            Log.d(TAG, "Activity started with city: " + city);
        } else {
            lat = intent.getDoubleExtra("lat", 0.0);
            lon = intent.getDoubleExtra("lon", 0d);
            Log.d(TAG, "Activity started with lat/long: " + lat + ", " + lon);
        }

        // this method triggers data cache load and api call in advance
        // it's lazy but at the end i didn't want to write two methods
        // todo: this should definitely be improved and modularized
        viewModel.fetchCurrentCityWeather(city, lat, lon);

        final FloatingActionButton favButton = findViewById(R.id.btn_fav);

        // floating button click listener that triggers the change of favourite status
        // for a city's weather data - it's a toggle
        favButton.setOnClickListener(v -> {
            Log.d(TAG, "Floating button clicked - tag: " + (String) favButton.getTag());

            boolean isFav = isFav((String) favButton.getTag());
            Log.d(TAG, "Floating button clicked - isFav: " + isFav);
            viewModel.setFavState(!isFav);
        });

        // various ui elements to be shown
        final TextView cityTv = findViewById(R.id.tv_city_name);
        final TextView lastUpdatedTv = findViewById(R.id.tv_last_updated);
        final TextView tempMinTv = findViewById(R.id.tv_min_temp);
        final TextView tempMaxTv = findViewById(R.id.tv_max_temp);
        final TextView tempTv = findViewById(R.id.tv_temp_large);
        final TextView tempFeelsTv = findViewById(R.id.tv_temp_desc);
        final TextView weatherDescTv = findViewById(R.id.tv_weather_desc);
        final ImageView weatherConditionTv = findViewById(R.id.iv_weather_condition_2x);

        // observing current weather live data via view model
        // both cached (from db) and network call gets updated and propagated here
        // as it changes and ui reflects the changes
        // a Pair<Boolean, WeatherData> is being used just to indicate whether the response was an error or not because even after an api call
        // i might have the cached data but ui should know the last had an error
        viewModel.getCurrentCityWeather().observe(this, resultPair -> {
            Log.d(TAG, "on changed called");

            if (resultPair != null && resultPair.first && resultPair.second != null) {
                Log.d(TAG, "setting views");
                StringBuilder stringBuilder = new StringBuilder();

                Picasso.get().load(Utils.getIconUrl(resultPair.second.weather_icon)).into(weatherConditionTv);

                cityTv.setText(stringBuilder.append(resultPair.second.city).append(", ").append(resultPair.second.country).toString());
                lastUpdatedTv.setText(Utils.lastUpdated(resultPair.second.last_updated));
                tempMinTv.setText(String.valueOf(resultPair.second.temp_min));
                tempMaxTv.setText(String.valueOf(resultPair.second.temp_max));

                stringBuilder.setLength(0);
                tempTv.setText(stringBuilder.append((resultPair.second.temp)).append("°").toString());

                stringBuilder.setLength(0);
                tempFeelsTv.setText(stringBuilder.append("Temperate at this time feels like ").append(resultPair.second.temp_feels_like).append("°").toString());
                weatherDescTv.setText(resultPair.second.weather_description);

                favButton.setVisibility(View.VISIBLE);

                // image resource for floating button is always added from data state right from first load
                // so that i don't have to do an if-else login outside
                favButton.setImageResource(getFavRes(resultPair.second.favourite));
                favButton.setTag(resultPair.second.favourite ? FAV_TAG : NOT_FAV_TAG);
            } else {
                // on error we are not doing anything else as we already have cached data and if not then
                // view will be empty --> an error view can be set by default i.e. at first launch
                Log.d(TAG, "not setting views --> some error: " + resultPair);
                Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // to toggle add/remove icon of floating button
    private int getFavRes(boolean favourite) {
        return favourite ? R.drawable.ic_close : R.drawable.ic_add;

    }

    // checking whether it's a fav already
    // this can be done with a call to view model as well
    // should have had just done that todo
    private boolean isFav(String tag) {
        return FAV_TAG.equals(tag);
    }
}
