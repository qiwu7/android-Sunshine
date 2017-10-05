package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class SunshineSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";
    private static boolean sInitialized = false;

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService.class)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncSunshineJob);
        Log.v(SUNSHINE_SYNC_TAG, "i am here");
    }

    public static void startImmediateSync(@NonNull final Context context) {
            Intent intentToSyncImmediately = new Intent(context, SunshineSyncIntentService.class);
            context.startService(intentToSyncImmediately);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        /*
          * We need to check to see if our ContentProvider has data to display in our forecast
          * list. However, performing a query on the main thread is a bad idea as this may
          * cause our UI to lag. Therefore, we create a thread in which we will run the query
          * to check the contents of our ContentProvider.
          */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                 /* URI for every row of weather data in our weather table*/
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                 /*
                  * Since this query is going to be used only as a check to see if we have any
                  * data (rather than to display data), we just need to PROJECT the ID of each
                  * row. In our queries where we display data, we need to PROJECT more columns
                  * to determine what weather details need to be displayed.
                  */
                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards();

                 /* Here, we perform the query to check to see if we have any weather data */
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);
                 /*
                  * A Cursor object can be null for various different reasons. A few are
                  * listed below.
                  *
                  *   1) Invalid URI
                  *   2) A certain ContentProvider's query method returns null
                  *   3) A RemoteException was thrown.
                  *
                  * Bottom line, it is generally a good idea to check if a Cursor returned
                  * from a ContentResolver is null.
                  *
                  * If the Cursor was null OR if it was empty, we need to sync immediately to
                  * be able to display data to the user.
                  */
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                 /* Make sure to close the Cursor to avoid memory leaks! */
                cursor.close();
            }
        });

         /* Finally, once the thread is prepared, fire it off to perform our checks. */
        checkForEmpty.start();

    }
}
