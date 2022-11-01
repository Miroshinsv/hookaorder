
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetaDataProperty {

    @JsonProperty("GeocoderMetaData")
    private GeocoderMetaData geocoderMetaData;
    @JsonProperty("GeocoderResponseMetaData")
    private GeocoderResponseMetaData geocoderResponseMetaData;

    public GeocoderMetaData getGeocoderMetaData() {
        return geocoderMetaData;
    }

    public void setGeocoderMetaData(GeocoderMetaData geocoderMetaData) {
        this.geocoderMetaData = geocoderMetaData;
    }

    public GeocoderResponseMetaData getGeocoderResponseMetaData() {
        return geocoderResponseMetaData;
    }

    public void setGeocoderResponseMetaData(GeocoderResponseMetaData geocoderResponseMetaData) {
        this.geocoderResponseMetaData = geocoderResponseMetaData;
    }

}
