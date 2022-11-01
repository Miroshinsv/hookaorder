
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeocoderResponseMetaData {

    @JsonProperty("found")
    private String found;
    @JsonProperty("request")
    private String request;
    @JsonProperty("results")
    private String results;
    @JsonProperty("suggest")
    private String suggest;

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

}
