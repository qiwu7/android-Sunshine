package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    /* TAG is used to output the log event */
    private String TAG = ForecastAdapter.class.getSimpleName();

    private Cursor mCursor;

    private final ForecastAdapterOnClickHandler mClickHandler;

    private final Context mContext;

    ForecastAdapter(Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View forecastView = inflater.inflate(R.layout.forecast_list_item, parent, false);
        forecastView.setFocusable(true);
        ForecastAdapterViewHolder viewHolder = new ForecastAdapterViewHolder(forecastView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        if (mCursor != null && mCursor.getCount() > 0) {
            // Move the cursor to the appropriate position
            mCursor.moveToPosition(position);
            // Generate a weather summary with the date, description, high and low
            /* Read date from the cursor */
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            /* Get human readable string using utility method */
            String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
            /* Use the weatherId to obtain the proper description */
            int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
            String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
            /* Read high temperature from the cursor (in degrees celsius) */
            double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
            /* Read low temperature from the cursor (in degrees celsius) */
            double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

            String highAndLowTemperature =
                    SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius);

            String weatherSummary = dateString + " - " + description + " - " + highAndLowTemperature;

            holder.mWeatherTextView.setText(weatherSummary);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }

    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }
}
