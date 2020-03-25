package com.example.photoweather.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ali Adel
 * <p>
 * Class to hold coulds info from json of weather
 */
public class Cloud {

    // clouds percentage
    @SerializedName("all")
    private int clouds;

    /**
     * @return clouds percentage
     */
    public int getClouds() {
        return this.clouds;
    }
}
