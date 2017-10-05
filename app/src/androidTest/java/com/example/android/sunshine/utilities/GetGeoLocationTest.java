package com.example.android.sunshine.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetGeoLocationTest {
    @Test
    public void testGetLocationCoordinates() {
        double[] locations = GetGeoLocation.getLocationCoordinates("MountainView, CA, 94043");
    }
}