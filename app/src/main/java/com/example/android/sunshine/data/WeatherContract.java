package com.example.android.sunshine.data;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.sunshine.R;
import com.example.android.sunshine.utilities.SunshineDateUtils;

public class WeatherContract {
    private WeatherContract(){}

    public final static String CONTENT_AUTHORITY = "com.example.android.sunshine";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     *     content://com.example.android.sunshine/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     */
    public final static String PATH_WEATHER = "weather";

    public final static class WeatherEntry implements BaseColumns {
        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public final static Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        /* Used internally as the name of our weather table. */
        public final static String TABLE_NAME = "weather";
        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular weather row represents.
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
        public final static String COLUMN_DATE = "date";
        /* Weather ID as returned by API, used to identify the icon to be used */
        public final static String COLUMN_WEATHER_ID = "weather_id";
        /* Min and max temperatures in °C for the day (stored as floats in the database) */
        public final static String COLUMN_MIN_TEMP ="min";
        public final static String COLUMN_MAX_TEMP = "max";
        /* Humidity is stored as a float representing percentage */
        public final static String COLUMN_HUMIDITY = "humidity";
        /* Pressure is stored as a float representing percentage */
        public final static String COLUMN_PRESSURE = "pressure";
        /* Wind speed is stored as a float representing wind speed in mph */
        public final static String COLUMN_WIND_SPEED = "wind";
        /*
         * Degrees are meteorological degrees (e.g, 0 is north, 180 is south).
         * Stored as floats in the database.
         */
        public final static String COLUMN_DEGREES = "degrees";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param date Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }
}
