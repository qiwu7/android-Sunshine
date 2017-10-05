package com.example.android.sunshine.utilities;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GetGeoLocation {
    private static final String GOOGLE_MAP_BASE_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String TAG = GetGeoLocation.class.getSimpleName();

    public static double[] getLocationCoordinates(String address) {
        Uri uri = Uri.parse(GOOGLE_MAP_BASE_URL + address).buildUpon().build();
        double[] locations = new double[2];
        try {
            URL url = new URL(uri.toString());
            String jsonGeoResponse = NetworkUtils
                    .getResponseFromHttpUrl(url);
            JSONObject resultJson = new JSONObject(jsonGeoResponse);

            if (resultJson.has("results")) {
                JSONObject jsonObject = resultJson.getJSONArray("results").getJSONObject(0);
                if (jsonObject.has("geometry")) {
                    JSONObject geometry = jsonObject.getJSONObject("geometry");
                    if (geometry.has("location")) {
                        JSONObject location = geometry.getJSONObject("location");
                        Double lat = location.getDouble("lat");
                        Double lng = location.getDouble("lng");
                        locations[0] = lat;
                        locations[1] = lng;
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
