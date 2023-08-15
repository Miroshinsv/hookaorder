package ru.hookaorder.backend.feature.JWT.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(UserEntity user) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(user.getRolesSet().stream().
            map(val -> ERole.valueOf(val.getRoleName()))
            .collect(Collectors.toSet()));
        jwtInfoToken.setUserId(user.getId());
        return jwtInfoToken;
    }
}