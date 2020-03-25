package com.example.photoweather.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ali Adel
 * <p>
 * Holds wind information from JSON object (wind speed)
 */
public class Wind {

    // wind speed in meter/sec
    @SerializedName("speed")
    private double speed;

    /**
     * @return wind speed in meter/sec
     */
    public double getSpeed() {
        return this.speed;
    }
}
