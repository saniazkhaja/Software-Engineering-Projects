package edu.uiuc.cs427app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Properties implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("mapbox_id")
    private String mapboxId;

    @SerializedName("feature_type")
    private String featureType;

    @SerializedName("full_address")
    private String fullAddress;

    @SerializedName("name")
    private String name;

    @SerializedName("name_preferred")
    private String namePreferred;

    @SerializedName("coordinates")
    private Coordinates coordinates;

    @SerializedName("place_formatted")
    private String placeFormatted;

    /**
     * Constructs a new Properties object.
     *
     * @param name        the full address or name of the location.
     * @param coordinates the geographical coordinates (latitude and longitude) of the location.
     */
    public Properties(String name, Coordinates coordinates) {
        this.fullAddress = name;
        this.coordinates = coordinates;
    }

    @SerializedName("bbox")
    private List<Double> bbox;

    public String getMapboxId() {
        return mapboxId;
    }

    public String getFeatureType() {
        return featureType;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getName() {
        return name;
    }

    public String getNamePreferred() {
        return namePreferred;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getPlaceFormatted() {
        return placeFormatted;
    }

    public List<Double> getBbox() {
        return bbox;
    }
}
