package ru.hookaorder.backend.feature.user.service;

import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private static final int PASSWORD_LENGTH_VALID = 8;
    private static final Optional<UserEntity> EMPTY_USER = Optional.empty();
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public Optional<UserEntity> create(UserEntity userEntity) {
        String password = userEntity.getPassword();
        if (Objects.isNull(password) || password.length() < PASSWORD_LENGTH_VALID) {
            return EMPTY_USER;
        }
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        return Optional.of(userRepository.save(userEntity));
    }

    @Override
    public Optional<UserEntity> update(Long id, UserEntity userEntity, Authentication authentication) {
        return userRepository.findById(id).map((user) -> {
            if (!(user.getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN))) {
                return EMPTY_USER;
            }
            NullAwareBeanUtilsBean.copyNoNullProperties(userEntity, user);
            return Optional.of(userRepository.save(user));
        }).orElse(EMPTY_USER);
    }

    @Override
    public boolean delete(Long id) {
        return userRepository.findById(id).map((user) -> {
            userRepository.deleteById(user.getId());
            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }
}
