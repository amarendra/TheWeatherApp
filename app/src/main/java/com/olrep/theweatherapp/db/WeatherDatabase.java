package com.olrep.theweatherapp.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.olrep.theweatherapp.contracts.WeatherCallback;
import com.olrep.theweatherapp.entity.WeatherData;
import com.olrep.theweatherapp.utils.Constants;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = WeatherData.class, version = 3, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    private final static String TAG = Constants.TAG + "WDB";

    private static WeatherDatabase instance;

    public abstract WeatherDao weatherDao();

    public static synchronized WeatherDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WeatherDatabase.class, "weather_database")
                    .fallbackToDestructiveMigration()   // will need to change this if on migration data retention is needed - a custom strategy might be needed to retain data
                    .allowMainThreadQueries()   // this is the dirty hack/shortcut which should not have been done - but this saved me a lot of time and for a small db it's just fine
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG, "DB onCreate called");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d(TAG, "DB onOpen called");
            // load DAOs
        }
    };

    // ========== TODO ==================
    // again this can be used to offload any initial loading on db when it creates or udpates
    // for example modifying data if a new schema is added -- but migration strategy would require change
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());


    private <R> void execute(Callable<R> callable, WeatherCallback<R> callback) {
        executor.execute(() -> {
            try {
                final R result = callable.call();
                callback.onSuccess(result);
            } catch (Exception ex) {
                callback.onError(ex);
            }
        });
    }
}
