package ru.hookaorder.backend.feature.image.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageRequest {
    @JsonProperty(value = "base64EncodedImage")
    private String base64EncodedImage;
}
