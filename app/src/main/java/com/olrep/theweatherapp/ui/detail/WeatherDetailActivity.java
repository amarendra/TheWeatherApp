package com.olrep.theweatherapp.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.ui.main.FavouritesViewModel;
import com.olrep.theweatherapp.utils.Constants;
import com.olrep.theweatherapp.utils.Utils;
import com.squareup.picasso.Picasso;

public class WeatherDetailActivity extends AppCompatActivity {
    private final String TAG = Constants.TAG + "WDA";

    private WeatherDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_detail_activity);

        //viewModel = new ViewModelProvider(this).get(WeatherDetailViewModel.class);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(WeatherDetailViewModel.class);

        Intent intent = getIntent();

        String city = null;
        if (intent.hasExtra("city")) {
            city = intent.getStringExtra("city");
        }

        viewModel.fetchCurrentCityWeather(city);

        final TextView cityTv = findViewById(R.id.tv_city_name);
        final TextView lastUpdatedTv = findViewById(R.id.tv_last_updated);
        final TextView tempMinTv = findViewById(R.id.tv_min_temp);
        final TextView tempMaxTv = findViewById(R.id.tv_max_temp);
        final ImageView weatherConditionTv = findViewById(R.id.iv_weather_condition_2x);

        viewModel.getCurrentCityWeather().observe(this, resultPair -> {
            Log.d(TAG, "on changed called");

            if (resultPair != null && resultPair.first && resultPair.second != null) {
                Log.d(TAG, "setting views");
                cityTv.setText(resultPair.second.city);
                lastUpdatedTv.setText(Utils.lastUpdated(resultPair.second.last_updated));
                tempMinTv.setText(String.valueOf(resultPair.second.temp_min));
                tempMaxTv.setText(String.valueOf(resultPair.second.temp_max));
                Picasso.get().load(Utils.getIconUrl(resultPair.second.weather_icon)).into(weatherConditionTv);
            } else {
                Log.d(TAG, "not setting views --> some error: " + resultPair);
                Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
