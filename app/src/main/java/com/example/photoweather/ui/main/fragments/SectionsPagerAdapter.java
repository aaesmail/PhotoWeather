package com.example.photoweather.ui.main.fragments;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.photoweather.R;
import com.example.photoweather.ui.main.fragments.history.HistoryFragment;
import com.example.photoweather.ui.main.fragments.weatherinfo.WeatherInfoFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    // tabe titles of fragments stored in @strings
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    /**
     * @param context of main activity
     * @param fm      fragment manager to adjust fragments
     */
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * @param position takes position in screen (0, 1) because we only have 2 fragments
     * @return corresponding fragment
     * 0 => weatherInfoFragment
     * 1 => history fragment
     */
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a WeatherInfoFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new WeatherInfoFragment();
            default:
                return new HistoryFragment();
        }
    }

    /**
     * to get title of fragment
     *
     * @param position of fragment (0, 1) because we only have 2 fragments
     * @return fragment name
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     * total number of fragments
     *
     * @return size of TAB_TITLES
     */
    @Override
    public int getCount() {
        // Show 2 total pages because only 2 fragments
        return TAB_TITLES.length;
    }
}