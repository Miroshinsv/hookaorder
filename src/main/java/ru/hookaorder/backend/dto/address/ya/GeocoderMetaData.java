
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeocoderMetaData {

    @JsonProperty("Address")
    private Address address;
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("precision")
    private String precision;
    @JsonProperty("text")
    private String text;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
