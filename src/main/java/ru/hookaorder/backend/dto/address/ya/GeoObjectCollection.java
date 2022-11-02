
package ru.hookaorder.backend.dto.address.ya;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GeoObjectCollection {

    @JsonProperty("featureMember")
    private List<FeatureMember> featureMember;
    @JsonProperty("metaDataProperty")
    private MetaDataProperty metaDataProperty;

    public List<FeatureMember> getFeatureMember() {
        return featureMember;
    }

    public void setFeatureMember(List<FeatureMember> featureMember) {
        this.featureMember = featureMember;
    }

    public MetaDataProperty getMetaDataProperty() {
        return metaDataProperty;
    }

    public void setMetaDataProperty(MetaDataProperty metaDataProperty) {
        this.metaDataProperty = metaDataProperty;
    }

}
