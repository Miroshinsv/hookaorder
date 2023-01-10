
package ru.hookaorder.backend.dto.address.ya;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

    @JsonProperty("Components")
    private List<Component> components;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("formatted")
    private String formatted;
    @JsonProperty("postal_code")
    private String postalCode;

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
