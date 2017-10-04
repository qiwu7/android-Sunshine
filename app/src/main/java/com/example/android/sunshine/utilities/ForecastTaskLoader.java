package com.example.android.sunshine.utilities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.sunshine.ForecastAdapter;
import com.example.android.sunshine.MainActivity;
import com.example.android.sunshine.data.SunshinePreferences;

import java.net.URL;

public class ForecastTaskLoader extends AsyncTaskLoader<String[]> {

    private ProgressBar mLoadingIndicator;

     /* This String array will hold and help cache our weather data */
     private String[] mWeatherData = null;

    public ForecastTaskLoader(Context context, ProgressBar LoadingIndicator) {
        super(context);
        mLoadingIndicator = LoadingIndicator;
    }

    @Override
    protected void onStartLoading() {
        if (mWeatherData != null) {
            deliverResult(mWeatherData);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Override
    public String[] loadInBackground() {
        URL weatherRequestUrl = NetworkUtils.getUrl(getContext());
        try {
            String jsonWeatherResponse = NetworkUtils
                    .getResponseFromHttpUrl(weatherRequestUrl);
            String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                    .getSimpleWeatherStringsFromJson(getContext(), jsonWeatherResponse);
            return simpleJsonWeatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deliverResult(String[] data) {
        mWeatherData = data;
        super.deliverResult(data);
    }
}
