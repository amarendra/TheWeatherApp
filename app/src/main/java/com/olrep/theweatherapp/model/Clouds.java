
package com.olrep.theweatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * this class and all the model classes in this package just get all the data from owm current weather
 * api - it is not at all used in any business logic
 */
public class Clouds {
    @SerializedName("all")
    @Expose
    private int all;

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return "Clouds{" +
                "all=" + all +
                '}';
    }
}
