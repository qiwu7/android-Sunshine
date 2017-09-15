package com.example.android.sunshine.data;

import android.provider.BaseColumns;

public class WeatherContract {
    private WeatherContract(){}

    public final static class WeatherEntry implements BaseColumns {
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
    }
}
