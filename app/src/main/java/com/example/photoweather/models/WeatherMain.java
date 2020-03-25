package com.example.photoweather.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ali Adel
 * <p>
 * holds weather temperature and humidity information from JSON object
 */
public class WeatherMain {

    // temperature in kelvin
    @SerializedName("temp")
    private double temperature;

    // what temperature feels like in kelvin
    @SerializedName("feels_like")
    private double feels_like;

    // minimum temperature in kelvin
    @SerializedName("temp_min")
    private double min;

    // maximum temperature in kelvin
    @SerializedName("temp_max")
    private double max;

    // humidity percentage
    @SerializedName("humidity")
    private int humidity;

    /**
     * @return temperature in °C
     */
    public double getTemperature() {
        return temperature - 273.15;
    }

    /**
     * @return temperature feels like in °C
     */
    public double getFeels_like() {
        return feels_like - 273.15;
    }

    /**
     * @return minimum temperature in °C
     */
    public double getMin() {
        return min - 273.15;
    }

    /**
     * @return maximum temperature in °C
     */
    public double getMax() {
        return max - 273.15;
    }

    /**
     * @return humidity percentage
     */
    public int getHumidity() {
        return humidity;
    }
}
