package com.example.photoweather.ui.main.fragments.weatherinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.photoweather.R;
import com.example.photoweather.models.Weather;

import java.util.Locale;

/**
 * @author Ali Adel
 * <p>
 * Fragment to show weather information and get weather info and display it
 */
public class WeatherInfoFragment extends Fragment {

    // hold reference to view model
    private WeatherInfoViewModel mWeatherInfoViewModel;

    // hold reference to text views that display information
    private TextView mCityTextView;
    private TextView mCloudPercentageTextView;
    private TextView mWindSpeedTextView;
    private TextView mMainWeatherTextView;
    private TextView mWeatherDescriptionTextView;
    private TextView mTemperatureTextView;
    private TextView mMinTemperatureTextView;
    private TextView mMaxTemperatureTextView;
    private TextView mFeelsLikeTemperatureTextView;
    private TextView mHumidityPercentageTextView;
    private View mWeatherDescriptionContainer;

    // containers that hold state of data
    private View mWholeWeatherDataContainer;
    private TextView mErrorMessage;
    private ProgressBar mLoadingBar;

    // search edit text
    private EditText mEnterCityEditText;


    /**
     * Get reference to view model
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherInfoViewModel = ViewModelProviders.of(this).get(WeatherInfoViewModel.class);
    }

    /**
     * inflate layout to display
     * get reference to all views on screen
     *
     * @return view to display
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // get weather info fragment to display
        View root = inflater.inflate(R.layout.fragment_weather_info, container, false);

        // get reference to all views
        mCityTextView = root.findViewById(R.id.city);
        mCloudPercentageTextView = root.findViewById(R.id.cloud);
        mWindSpeedTextView = root.findViewById(R.id.speed);
        mMainWeatherTextView = root.findViewById(R.id.main);
        mWeatherDescriptionTextView = root.findViewById(R.id.desc);
        mTemperatureTextView = root.findViewById(R.id.temp);
        mMinTemperatureTextView = root.findViewById(R.id.min);
        mMaxTemperatureTextView = root.findViewById(R.id.max);
        mFeelsLikeTemperatureTextView = root.findViewById(R.id.like);
        mHumidityPercentageTextView = root.findViewById(R.id.humidity);

        // container that has weather detailed description
        // because want to hide it if short description matches detailed description
        mWeatherDescriptionContainer = root.findViewById(R.id.weather_desc_container);

        // whole container of data and error message and loading bar
        mWholeWeatherDataContainer = root.findViewById(R.id.data_container);
        mErrorMessage = root.findViewById(R.id.error_text);
        mLoadingBar = root.findViewById(R.id.progress_bar);

        // set visibility of all to gone for now
        mWholeWeatherDataContainer.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.GONE);

        // edit text
        mEnterCityEditText = root.findViewById(R.id.city_edit_text);

        // set listener of button that tells view model to fetch data
        root.findViewById(R.id.find_city_button).setOnClickListener(v -> {
            // if nothing in edit text then do nothing
            if (TextUtils.isEmpty(mEnterCityEditText.getText()))
                return;

            // tell view model to update weather with data in edit text
            mWeatherInfoViewModel.updateWeather(mEnterCityEditText.getText().toString());
            // show loading bar and hide other views
            mWholeWeatherDataContainer.setVisibility(View.GONE);
            mErrorMessage.setVisibility(View.GONE);
            mLoadingBar.setVisibility(View.VISIBLE);
        });

        // get weather and observe it and go to update UI
        mWeatherInfoViewModel.getWeather().observe(getViewLifecycleOwner(), this::updateUi);

        // return root view to display
        return root;
    }

    /**
     * helper method to update UI with weather object
     *
     * @param weather object containing weather info
     */
    private void updateUi(Weather weather) {

        // finished getting data from view model so remove loading bar
        mLoadingBar.setVisibility(View.GONE);

        // if network is available then update error message accordingly
        if (isNetworkAvailable()) {
            mErrorMessage.setText(getString(R.string.error_city_not_found));
        } else {
            mErrorMessage.setText(getString(R.string.error_no_network));
        }

        // if view model failed to get data then weather is null and show error message
        // and then return from function
        if (weather == null) {
            mWholeWeatherDataContainer.setVisibility(View.GONE);
            mErrorMessage.setVisibility(View.VISIBLE);
            return;
        }

        // Data is not null and is valid so display it in UI and remove error message
        mWholeWeatherDataContainer.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);

        // set city name and cloud percentage
        mCityTextView.setText(weather.getCity());
        mCloudPercentageTextView.setText(getString(R.string.data_with_percentage, weather.getCloudiness()));
        mWindSpeedTextView.setText(getString(R.string.wind_speed, String.valueOf(weather.getWindSpeed())));
        mMainWeatherTextView.setText(weather.getMain());

        // remove detailed description if matches short description of weather
        if (weather.getMain().equalsIgnoreCase(weather.getDescription())) {
            mWeatherDescriptionContainer.setVisibility(View.GONE);
        }

        // set detailed description
        mWeatherDescriptionTextView.setText(weather.getDescription());

        // set temperature, min, max and feels like text
        mTemperatureTextView.setText(getString(R.string.temperature_format,
                String.format(Locale.US, "%.2f",
                        weather.getTemperature())));
        mMinTemperatureTextView.setText(getString(R.string.temperature_format,
                String.format(Locale.US, "%.2f",
                        weather.getMin())));
        mMaxTemperatureTextView.setText(getString(R.string.temperature_format,
                String.format(Locale.US, "%.2f",
                        weather.getMax())));
        mFeelsLikeTemperatureTextView.setText(getString(R.string.temperature_format,
                String.format(Locale.US, "%.2f",
                        weather.getFeelsLike())));

        // set humidity text
        mHumidityPercentageTextView.setText(getString(R.string.data_with_percentage, weather.getHumidity()));
    }

    /**
     * Helper method to check for network connection
     *
     * @return true if there is network connection
     * false if no network connection
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}