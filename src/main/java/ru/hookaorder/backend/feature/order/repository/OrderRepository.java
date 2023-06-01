package ru.hookaorder.backend.feature.order.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.List;

public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByUserId(UserEntity userEntity);
    List<OrderEntity> findAllByUserId(UserEntity userEntity, Pageable pageable);
    List<OrderEntity> findAllByPlaceId(PlaceEntity placeEntity);
    List<OrderEntity> findAllByPlaceIdAndUserId(PlaceEntity placeEntity, UserEntity userEntity);
    List<OrderEntity> findAllByUserIdAndOrderStatus(UserEntity userEntity, EOrderStatus orderStatus, Pageable pageable);

    @Query(value = "SELECT * FROM orders ord " +
            "WHERE ord.places_id = ?1 " +
            "AND ord.taken_at IS NULL AND ord.completed_at IS NULL AND ord.cancelled_at IS NULL",
            nativeQuery = true)
    List<OrderEntity> findNewByPlaceId(PlaceEntity placeEntity);

    @Query(value = "SELECT * FROM orders ord " +
            "WHERE ord.places_id = ?1 AND ord.user_id = ?2 " +
            "AND ord.taken_at IS NULL AND ord.completed_at IS NULL AND ord.cancelled_at IS NULL",
            nativeQuery = true)
    List<OrderEntity> findNewByPlaceIdAndUserId(PlaceEntity placeEntity, UserEntity userEntity);
}