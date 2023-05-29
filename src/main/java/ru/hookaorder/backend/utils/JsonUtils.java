package ru.hookaorder.backend.utils;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.List;

public final class JsonUtils {

  private static final String PHONE = "phone";
  private static final String PHONE_FILTER = "phoneFilter";

  private static final List<ERole> PHONE_VIEW_ALLOWED_ROLES = List.of(
      ERole.ADMIN, ERole.OWNER, ERole.WAITER, ERole.HOOKAH_MASTER
  );

  private JsonUtils() {
  }

  public static MappingJacksonValue checkAndApplyPhoneFilter(BaseEntity entity, Authentication authentication) {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(entity);

    if (!isOwnUserInfo(entity, authentication) && isFilterNeeded(authentication)) {
      SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(PHONE);
      FilterProvider filterProvider = new SimpleFilterProvider().addFilter(PHONE_FILTER, simpleBeanPropertyFilter);
      mappingJacksonValue.setFilters(filterProvider);
    }

    return mappingJacksonValue;
  }

  public static MappingJacksonValue checkAndApplyPhoneFilterForList(List<? extends BaseEntity> entities, Authentication authentication) {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(entities);

    if (isFilterNeeded(authentication)) {
      SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(PHONE);
      FilterProvider filterProvider = new SimpleFilterProvider().addFilter(PHONE_FILTER, simpleBeanPropertyFilter);
      mappingJacksonValue.setFilters(filterProvider);
    }

    return mappingJacksonValue;
  }

  private static boolean isOwnUserInfo(BaseEntity entity, Authentication authentication) {
    return (entity instanceof UserEntity) && (((UserEntity) entity).getId().equals(authentication.getPrincipal()));
  }

  private static boolean isFilterNeeded(Authentication authentication) {
    return authentication.getAuthorities().isEmpty()
        || !(PHONE_VIEW_ALLOWED_ROLES.stream().anyMatch(authentication.getAuthorities()::contains));
  }
}
