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

public class WeatherDetailActivity extends AppCompatActivity {
    private final String TAG = Constants.TAG + "WDA";

    private final String FAV_TAG = "is_fav";
    private final String NOT_FAV_TAG = "is_not_fav";

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

        final FloatingActionButton favButton = findViewById(R.id.btn_fav);
        favButton.setOnClickListener(v -> {
            Log.d(TAG, "Floating button clicked - tag: " + (String) favButton.getTag());

            boolean isFav = isFav((String) favButton.getTag());
            Log.d(TAG, "Floating button clicked - isFav: " + isFav);
            viewModel.setFavState(!isFav);
        });

        final TextView cityTv = findViewById(R.id.tv_city_name);
        final TextView lastUpdatedTv = findViewById(R.id.tv_last_updated);
        final TextView tempMinTv = findViewById(R.id.tv_min_temp);
        final TextView tempMaxTv = findViewById(R.id.tv_max_temp);
        final TextView tempTv = findViewById(R.id.tv_temp_large);
        final TextView tempFeelsTv = findViewById(R.id.tv_temp_desc);
        final TextView weatherDescTv = findViewById(R.id.tv_weather_desc);
        final ImageView weatherConditionTv = findViewById(R.id.iv_weather_condition_2x);

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

                favButton.setImageResource(getFavRes(resultPair.second.favourite));
                favButton.setTag(resultPair.second.favourite ? FAV_TAG : NOT_FAV_TAG);
            } else {
                Log.d(TAG, "not setting views --> some error: " + resultPair);
                Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getFavRes(boolean favourite) {
        return favourite ? R.drawable.ic_close : R.drawable.ic_add;

    }

    private boolean isFav(String tag) {
        return FAV_TAG.equals(tag);
    }
}
