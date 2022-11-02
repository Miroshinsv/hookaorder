
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoObject {

    @JsonProperty("boundedBy")
    private BoundedBy boundedBy;
    @JsonProperty("description")
    private String description;
    @JsonProperty("metaDataProperty")
    private MetaDataProperty metaDataProperty;
    @JsonProperty("name")
    private String name;
    @JsonProperty("Point")
    private Point point;

    public BoundedBy getBoundedBy() {
        return boundedBy;
    }

    public void setBoundedBy(BoundedBy boundedBy) {
        this.boundedBy = boundedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MetaDataProperty getMetaDataProperty() {
        return metaDataProperty;
    }

    public void setMetaDataProperty(MetaDataProperty metaDataProperty) {
        this.metaDataProperty = metaDataProperty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

}
