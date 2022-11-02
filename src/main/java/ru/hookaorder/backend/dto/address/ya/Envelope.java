
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Envelope {

    @JsonProperty("lowerCorner")
    private String lowerCorner;
    @JsonProperty("upperCorner")
    private String upperCorner;

    public String getLowerCorner() {
        return lowerCorner;
    }

    public void setLowerCorner(String lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    public String getUpperCorner() {
        return upperCorner;
    }

    public void setUpperCorner(String upperCorner) {
        this.upperCorner = upperCorner;
    }

}
