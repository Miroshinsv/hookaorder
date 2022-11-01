
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

    @JsonProperty("GeoObjectCollection")
    private GeoObjectCollection geoObjectCollection;

    public GeoObjectCollection getGeoObjectCollection() {
        return geoObjectCollection;
    }

    public void setGeoObjectCollection(GeoObjectCollection geoObjectCollection) {
        this.geoObjectCollection = geoObjectCollection;
    }

}
