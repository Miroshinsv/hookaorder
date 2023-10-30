package ru.hookaorder.backend.feature.place.service;

import org.springframework.security.core.Authentication;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;

import java.util.Optional;

public interface PlaceService {
    Optional<PlaceEntity> create(PlaceEntity place, Authentication authentication);

    Optional<PlaceEntity> update(Long id, PlaceEntity place, Authentication authentication);

    Optional<String> uploadImage(Long id, String base64EncodedImage);

    boolean delete(Long id);
}
