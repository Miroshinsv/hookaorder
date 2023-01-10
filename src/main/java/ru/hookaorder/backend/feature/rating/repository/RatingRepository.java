package ru.hookaorder.backend.feature.rating.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hookaorder.backend.feature.rating.entity.RatingEntity;

public interface RatingRepository extends CrudRepository<RatingEntity, Long> {

}
