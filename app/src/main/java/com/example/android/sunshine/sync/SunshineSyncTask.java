package com.example.android.sunshine.sync;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {

    public static void syncWeather(Context context) {
        URL weatherRequestUrl = NetworkUtils.getUrl(context);
        /* Use the URL to retrieve the JSON */
        try {
            String jsonWeatherResponse = NetworkUtils
                    .getResponseFromHttpUrl(weatherRequestUrl);
            ContentValues[] weatherData = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromOWMJson(context, jsonWeatherResponse);
            if (weatherData != null && weatherData.length != 0) {
                ContentResolver cr = context.getContentResolver();
                cr.delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
                cr.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, weatherData);

                boolean notificationsEnabled = SunshinePreferences
                        .areNotificationsEnabled(context);
                long timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context);
                boolean oneDayPassedSinceLastNotification = false;
                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }
                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
