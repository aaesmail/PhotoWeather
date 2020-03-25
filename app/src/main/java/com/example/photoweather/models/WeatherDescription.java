package com.example.photoweather.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ali Adel
 * <p>
 * holds weather description from JSON
 */
public class WeatherDescription {

    // weather description in 1 or 2 words
    @SerializedName("main")
    private String main;

    // detailed description of weather
    @SerializedName("description")
    private String description;

    /**
     * @return detailed description of weather
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return short description of weather in 1 or 2 words
     */
    public String getMain() {
        return this.main;
    }
}
