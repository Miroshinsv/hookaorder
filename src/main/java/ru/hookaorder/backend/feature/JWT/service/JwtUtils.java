package ru.hookaorder.backend.feature.JWT.service;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.repository.UserRepository;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public final class JwtUtils {
    private static UserRepository userRepository;

    @Autowired
    public JwtUtils(UserRepository userRepository) {
        JwtUtils.userRepository = userRepository;
    }

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(userRepository.findById(Long.valueOf(claims.getSubject())).get().getRolesSet().stream().map(val -> ERole.valueOf(val.getRoleName())).collect(Collectors.toSet()));
        jwtInfoToken.setUserId(Long.valueOf(claims.getSubject()));
        return jwtInfoToken;
    }
}