package ru.hookaorder.backend.feature.user.service;

import org.springframework.security.core.Authentication;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserEntity> create(UserEntity userEntity);
    Optional<UserEntity> update(Long id, UserEntity userEntity, Authentication authentication);
    boolean delete(Long id);
}
