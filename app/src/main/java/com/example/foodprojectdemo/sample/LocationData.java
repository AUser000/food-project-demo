package com.example.foodprojectdemo.sample;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Dhanushka Dharmasena on 16/07/2018.
 */
@IgnoreExtraProperties
public class LocationData {
    public double lat;
    public double lng;

    public LocationData() {}

    public LocationData(double lat, double lng) {

        this.lat = lat;
        this.lng = lng;
    }

}
