package com.example.photoweather.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Ali Adel
 * <p>
 * Class to hold weather object which corresponds to JSON object got from openweathermap API
 */
public class Weather {

    // city name
    @SerializedName("name")
    private String name;

    // array of objects which hold weather description
    @SerializedName("weather")
    private List<WeatherDescription> weatherDescription;

    // object to hold temperature and humidity
    @SerializedName("main")
    private WeatherMain main;

    // object to hold wind info speed
    @SerializedName("wind")
    private Wind wind;

    // holds cloud percentage
    @SerializedName("clouds")
    private Cloud cloud;

    /**
     * @return city name
     */
    public String getCity() {
        return this.name;
    }

    /**
     * @return clouds percentage
     */
    public int getCloudiness() {
        return this.cloud.getClouds();
    }

    /**
     * @return wind speed in meter/sec
     */
    public double getWindSpeed() {
        return this.wind.getSpeed();
    }

    /**
     * @return weather main description
     */
    public String getMain() {
        return this.weatherDescription.get(0).getMain();
    }

    /**
     * @return weather detailed description
     */
    public String getDescription() {
        return this.weatherDescription.get(0).getDescription();
    }

    /**
     * @return temperature in °C
     */
    public double getTemperature() {
        return this.main.getTemperature();
    }

    /**
     * @return max temperature in °C
     */
    public double getMax() {
        return this.main.getMax();
    }

    /**
     * @return min temperature in °C
     */
    public double getMin() {
        return this.main.getMin();
    }

    /**
     * @return what temperature feels like in °C
     */
    public double getFeelsLike() {
        return this.main.getFeels_like();
    }

    /**
     * @return humidity in percentage
     */
    public int getHumidity() {
        return this.main.getHumidity();
    }

}
