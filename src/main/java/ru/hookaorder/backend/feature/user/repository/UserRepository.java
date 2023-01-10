package ru.hookaorder.backend.feature.user.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhone(String phone);
}