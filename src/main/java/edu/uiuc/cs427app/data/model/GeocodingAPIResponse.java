package edu.uiuc.cs427app.data.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodingAPIResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("features")
    private List<Feature> features;

    public String getType() {
        return type;
    }

    public List<Feature> getFeatures() {
        return features;
    }
}

class Geometry {
    @SerializedName("type")
    private String type;

    @SerializedName("coordinates")
    private List<Double> coordinates;

    public String getType() {
        return type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }
}

class Context {
    @SerializedName("district")
    private District district;

    @SerializedName("region")
    private Region region;

    @SerializedName("country")
    private Country country;

    @SerializedName("place")
    private Place place;

    public District getDistrict() {
        return district;
    }

    public Region getRegion() {
        return region;
    }

    public Country getCountry() {
        return country;
    }

    public Place getPlace() {
        return place;
    }
}

class District {
    @SerializedName("mapbox_id")
    private String mapboxId;

    @SerializedName("name")
    private String name;

    @SerializedName("wikidata_id")
    private String wikidataId;

    public String getMapboxId() {
        return mapboxId;
    }

    public String getName() {
        return name;
    }

    public String getWikidataId() {
        return wikidataId;
    }
}

class Region {
    @SerializedName("mapbox_id")
    private String mapboxId;

    @SerializedName("name")
    private String name;

    @SerializedName("wikidata_id")
    private String wikidataId;

    @SerializedName("region_code")
    private String regionCode;

    @SerializedName("region_code_full")
    private String regionCodeFull;

    public String getMapboxId() {
        return mapboxId;
    }

    public String getName() {
        return name;
    }

    public String getWikidataId() {
        return wikidataId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getRegionCodeFull() {
        return regionCodeFull;
    }
}

class Country {
    @SerializedName("mapbox_id")
    private String mapboxId;

    @SerializedName("name")
    private String name;

    @SerializedName("wikidata_id")
    private String wikidataId;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("country_code_alpha_3")
    private String countryCodeAlpha3;

    public String getMapboxId() {
        return mapboxId;
    }

    public String getName() {
        return name;
    }

    public String getWikidataId() {
        return wikidataId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryCodeAlpha3() {
        return countryCodeAlpha3;
    }
}

class Place {
    @SerializedName("mapbox_id")
    private String mapboxId;

    @SerializedName("name")
    private String name;

    @SerializedName("wikidata_id")
    private String wikidataId;

    public String getMapboxId() {
        return mapboxId;
    }

    public String getName() {
        return name;
    }

    public String getWikidataId() {
        return wikidataId;
    }
}
