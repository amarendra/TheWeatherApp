package com.olrep.theweatherapp.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.contracts.ClickListener;
import com.olrep.theweatherapp.model.CurrentWeather;
import com.olrep.theweatherapp.ui.detail.WeatherDetailActivity;
import com.olrep.theweatherapp.utils.Constants;

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
        favouritesViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_favs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final FavAdapter adapter = new FavAdapter();
        recyclerView.setAdapter(adapter);

        favouritesViewModel.getFavourites().observe(this, currentWeathers -> adapter.submitList(currentWeathers));

        EditText cityInput = view.findViewById(R.id.et_search_city);
        Button searchButton = view.findViewById(R.id.btn_search);
        searchButton.setOnClickListener(v -> {
            Editable city = cityInput.getText();

            if (!TextUtils.isEmpty(city)) {
                Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
                intent.putExtra("city", cityInput.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(final CurrentWeather currentWeather) {
        if (!isDetached() && getActivity() != null && !getActivity().isFinishing()) {
            String city = currentWeather.getName();
            Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        }
    }
}