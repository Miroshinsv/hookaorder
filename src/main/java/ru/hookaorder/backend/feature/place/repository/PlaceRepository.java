package ru.hookaorder.backend.feature.place.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends CrudRepository<PlaceEntity, Long> {
    Optional<PlaceEntity> findById(Long id);

    List<PlaceEntity> findAll();

    void deleteById(Long id);

    PlaceEntity save(PlaceEntity placeEntity);
}
