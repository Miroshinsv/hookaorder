package ru.hookaorder.backend.feature.JWT.service;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.hookaorder.backend.feature.roles.entity.ERole;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setPhone(claims.getSubject());
        jwtInfoToken.setUserId(claims.get("userId", Integer.class));
        return jwtInfoToken;
    }

    private static Set<ERole> getRoles(Claims claims) {
        final List<LinkedHashMap> roles = claims.get("roles", List.class);
        return roles.stream().map((value) -> ERole.valueOf((String) value.get("role_name"))).collect(Collectors.toSet());
    }

}