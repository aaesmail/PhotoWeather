package com.example.photoweather;

import com.example.photoweather.models.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Ali Adel
 * <p>
 * Interface to be used by retrofit to make Network calls
 */
public interface WeatherApi {

    /**
     * Used to get weather info from network API about certain city
     *
     * @param appId    app id that used to get authorization from network API
     * @param cityName city to get weather info of
     * @return Weather object that holds weather information about city
     */
    @GET("data/2.5/weather")
    Call<Weather> getWeather(@Query("appid") String appId,
                             @Query("q") String cityName);
}
