package com.olrep.theweatherapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.os.Bundle;
import android.view.WindowManager;

import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getSupportActionBar().setTitle(getString(R.string.action_search));

        // this hides the soft keypad which was always showing since edit text had focus on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}