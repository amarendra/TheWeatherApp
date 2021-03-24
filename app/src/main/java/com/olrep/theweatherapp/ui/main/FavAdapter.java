package com.olrep.theweatherapp.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.olrep.theweatherapp.R;
import com.olrep.theweatherapp.contracts.ClickListener;
import com.olrep.theweatherapp.entity.WeatherData;
import com.olrep.theweatherapp.utils.Utils;
import com.squareup.picasso.Picasso;

public class FavAdapter extends ListAdapter<WeatherData, FavAdapter.FavHolder> {
    private final ClickListener clickListener;

    protected FavAdapter(ClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = (ClickListener) clickListener;
    }

    private static final DiffUtil.ItemCallback<WeatherData> DIFF_CALLBACK = new DiffUtil.ItemCallback<WeatherData>() {
        @Override
        public boolean areItemsTheSame(@NonNull WeatherData oldItem, @NonNull WeatherData newItem) {
            return oldItem.city.equalsIgnoreCase(newItem.city);
        }

        @Override
        public boolean areContentsTheSame(@NonNull WeatherData oldItem, @NonNull WeatherData newItem) {
            return oldItem.city.equals(newItem.city);
        }
    };

    @NonNull
    @Override
    public FavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        return new FavHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavHolder holder, int position) {
        WeatherData weather = getItem(position);
        holder.textViewPlace.setText(weather.city);
        holder.textViewMinTemp.setText(String.valueOf(weather.temp_min));
        holder.textViewMaxTemp.setText(String.valueOf(weather.temp_max));
        Picasso.get().load(Utils.getIconUrl(weather.weather_icon)).into(holder.imageWeatherCondition);
    }

    class FavHolder extends RecyclerView.ViewHolder {
        private final TextView textViewPlace;
        private final ImageView imageWeatherCondition;
        private final TextView textViewMinTemp;
        private final TextView textViewMaxTemp;

        public FavHolder(View itemView) {
            super(itemView);

            textViewPlace = itemView.findViewById(R.id.tv_place);
            textViewMinTemp = itemView.findViewById(R.id.tv_min_temp);
            textViewMaxTemp = itemView.findViewById(R.id.tv_max_temp);
            imageWeatherCondition = itemView.findViewById(R.id.iv_weather_condition);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getItem(getAdapterPosition()));
                }
            });
        }
    }
}
