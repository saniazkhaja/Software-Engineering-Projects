package edu.uiuc.cs427app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Coordinates implements Serializable {
    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    /**
     * Constructs a new Coordinates object.
     *
     * @param longitude the longitude of the location.
     * @param latitude  the latitude of the location.
     */
    public Coordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
