package com.olrep.theweatherapp.ui.main;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.contracts.ClickListener;
import com.olrep.theweatherapp.entity.WeatherData;
import com.olrep.theweatherapp.ui.detail.WeatherDetailActivity;
import com.olrep.theweatherapp.utils.Constants;

import java.util.List;

public class MainFragment extends Fragment implements ClickListener {
    private final String TAG = Constants.TAG + "MF";

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
        final Button searchButton = view.findViewById(R.id.btn_search);

        final EditText cityInput = view.findViewById(R.id.et_search_city);

        searchButton.setOnClickListener(v -> {
            getTextForDetail(cityInput);
        });

        final ImageView gpsIcon = view.findViewById(R.id.iv_gps);
        gpsIcon.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                Location location = getLastKnownLocation();

                if (location != null) {
                    startDetailActivity(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    private void getTextForDetail(EditText cityInput) {
        Editable city = cityInput.getText();

        if (!TextUtils.isEmpty(city)) {
            startDetailActivity(city.toString());
        } else {
            Toast.makeText(getActivity(), "Enter a valid city name", Toast.LENGTH_LONG).show();
        }
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

    private void startDetailActivity(double lat, double lon) {
        if (!isDetached() && getActivity() != null && !getActivity().isFinishing()) {
            Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
            intent.putExtra("lat", String.valueOf(lat));
            intent.putExtra("lon", String.valueOf(lon));
            startActivity(intent);
        } else {
            Log.e(TAG, "Activity finishing, not calling detail activity");
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location permission needed")
                        .setMessage("Need location permission to search weather with lat-long")
                        .setPositiveButton("OK", (dialogInterface, i) ->
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION))
                        .create()
                        .show();
            } else {
                Log.d(TAG, "Asking for permission");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }

            Toast.makeText(getActivity(), "Please go to settings and provide location permission", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Some permission was granted");

                if (getActivity() != null & ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = getLastKnownLocation();

                    Log.d(TAG, "Last location received after permission grant: " + location);

                    if (location != null) {
                        startDetailActivity(location.getLatitude(), location.getLongitude());
                    } else {
                        Log.e(TAG, "null loc, stopping");
                    }
                } else {
                    Log.e(TAG, "loc perm wasn't granted");
                }
            } else {
                Log.e(TAG, "Permission was denied");
                Toast.makeText(getActivity(), "Permission was denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Location getLastKnownLocation() {
        Location bestLocation = null;

        try {
            LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);

            for (String provider : providers) {
                Location location = mLocationManager.getLastKnownLocation(provider);
                Log.d(TAG, "From provider: " + provider + " | loc: " + location);

                if (location != null && (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy())) {
                    bestLocation = location;
                }
            }
        } catch (SecurityException ex) {
            Log.e(TAG, "No location permission: " + Log.getStackTraceString(ex));
        } catch (Exception ex) {
            Log.e(TAG, "Ex in getLastKnownLocation: " + Log.getStackTraceString(ex));
        }

        return bestLocation;
    }
}