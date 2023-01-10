package ru.hookaorder.backend.utils;

import org.springframework.security.core.Authentication;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.roles.entity.ERole;

public class CheckOwnerAndRolesAccess {
    public static Boolean isOwnerOrAdmin(PlaceEntity place, Authentication authentication) {
        if (authentication.getAuthorities().contains(ERole.ADMIN)) return Boolean.TRUE;
        if (place.getOwner().getId() == null || !place.getOwner().getId().equals(authentication.getPrincipal())) return Boolean.FALSE;
        return Boolean.TRUE;
    }

}
