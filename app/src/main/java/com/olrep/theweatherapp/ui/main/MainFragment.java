package com.olrep.theweatherapp.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.contracts.ClickListener;
import com.olrep.theweatherapp.entity.WeatherData;
import com.olrep.theweatherapp.model.CurrentWeather;
import com.olrep.theweatherapp.ui.detail.WeatherDetailActivity;
import com.olrep.theweatherapp.utils.Constants;

import java.util.logging.Logger;

public class MainFragment extends Fragment implements ClickListener {
    private final String TAG = Constants.TAG + MainFragment.class.getSimpleName();

    private FavouritesViewModel favouritesViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View errorView = view.findViewById(R.id.ll_no_favs_or_search);

        RecyclerView recyclerView = view.findViewById(R.id.rv_favs);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setHasFixedSize(true);

        final FavAdapter adapter = new FavAdapter(this);
        recyclerView.setAdapter(adapter);

        //favouritesViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        favouritesViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication())).get(FavouritesViewModel.class);

        favouritesViewModel.getFavourites().observe(this, currentWeathers -> {
            if (currentWeathers != null && currentWeathers.size() > 0) {
                Log.d(TAG, "We have fav weathers");
                recyclerView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                adapter.submitList(currentWeathers);
            } else {
                Log.e(TAG, "No fav weathers");
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        });

        EditText cityInput = view.findViewById(R.id.et_search_city);
        Button searchButton = view.findViewById(R.id.btn_search);
        searchButton.setOnClickListener(v -> {
            Editable city = cityInput.getText();

            if (!TextUtils.isEmpty(city)) {
                startDetailActivity(city.toString());
            } else {
                Toast.makeText(getActivity(), "Enter a valid city name", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(final WeatherData weatherData) {
        Log.d(TAG, "Fav city clicked");

        if (weatherData != null) {
           startDetailActivity(weatherData.city);
        } else {
            Log.e(TAG, "Activity finishing, not calling detail activity");
        }
    }

    private void startDetailActivity(String city) {
        if (!isDetached() && getActivity() != null && !getActivity().isFinishing() && !TextUtils.isEmpty(city)) {
            Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        } else {
            Log.e(TAG, "Activity finishing, not calling detail activity");
        }
    }
}