package edu.uiuc.cs427app.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Feature implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("type")
    private String type;

    /**
     * Constructs a new Feature object.
     *
     * @param name        the name of the feature, such as a city or location.
     * @param type        the type of the feature (e.g., "city", "park").
     * @param coordinates the geographical coordinates (latitude and longitude) of the feature.
     */
    public Feature(String name, String type, Coordinates coordinates) {
        this.properties = new Properties(name, coordinates);
        this.type = type;
    }

    @SerializedName("id")
    private String id;

    @SerializedName("properties")
    private Properties properties;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Properties getProperties() {
        return properties;
    }

    @NonNull
    @Override
    public String toString() {
        return getProperties().getFullAddress();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Feature) {
            Feature otherFeature = (Feature) obj;
            return otherFeature.toString().equals(this.toString());
        }
        return false;
    }
}
