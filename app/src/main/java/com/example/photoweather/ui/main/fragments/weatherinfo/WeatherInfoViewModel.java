package com.example.photoweather.ui.main.fragments.weatherinfo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.photoweather.Repository;
import com.example.photoweather.models.Weather;

/**
 * @author Ali Adel
 * <p>
 * View model to hold weather information to display by UI
 */
public class WeatherInfoViewModel extends AndroidViewModel {

    // live data to observe weather
    private LiveData<Weather> mWeather;
    // repository instance
    private Repository mRepository;

    /**
     * Constructor to be used by Viewmodleproviders
     *
     * @param application context needed to give to respository
     */
    public WeatherInfoViewModel(@NonNull Application application) {
        super(application);

        // get repository instance
        mRepository = Repository.getRepositoryInstance(application);
        // set live data to watch
        mWeather = mRepository.getWeather();
    }

    /**
     * @return live data that watches change in weather information
     */
    public LiveData<Weather> getWeather() {
        return mWeather;
    }

    /**
     * Tell repository to update weather info
     *
     * @param cityName name of city to get weather data for
     */
    public void updateWeather(String cityName) {
        mRepository.updateWeather(cityName);
    }
}