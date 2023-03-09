package ru.hookaorder.backend.feature.roles.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum ERole implements GrantedAuthority {
    ADMIN("ADMIN"), OWNER("OWNER"), USER("USER"), HOOKAH_MASTER("HOOKAH_MASTER"), WAITER("WAITER");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}
