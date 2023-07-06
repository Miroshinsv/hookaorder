package ru.hookaorder.backend.feature.subscribe.controller;

import com.google.common.collect.Sets;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.subscribe.EPlaceNotifyMode;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.services.pushnotification.IPushNotificationService;
import ru.hookaorder.backend.utils.CheckOwnerAndRolesAccess;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping(value = "/place/subscribe")
@RestController
@AllArgsConstructor
public class SubscribeController {
  private final PlaceRepository placeRepository;
  private final UserRepository userRepository;
  private final IPushNotificationService pushNotificationService;

  @PostMapping("/add/{placeId}")
  public ResponseEntity<?> subscribeToPlace(@PathVariable Long placeId, Authentication authentication) {
    return placeRepository.findById(placeId).map((val) -> {
      Set<UserEntity> placeSubscribers = val.getSubscribers();
      placeSubscribers.add(userRepository.findById((Long) authentication.getPrincipal()).get());
      val.setSubscribers(placeSubscribers);
      placeRepository.save(val);
      return ResponseEntity.ok().build();
    }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/delete/{placeId}")
  public ResponseEntity<?> unsubscribeFromPlace(@PathVariable Long placeId, Authentication authentication) {
    return placeRepository.findById(placeId).map((val) -> {
      Set<UserEntity> placeSubscribers = val.getSubscribers();
      placeSubscribers.remove(userRepository.findById((Long) authentication.getPrincipal()).get());
      val.setSubscribers(placeSubscribers);
      placeRepository.save(val);
      return ResponseEntity.ok().build();
    }).orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
  @PostMapping("/add/all/{placeId}")
  public ResponseEntity<?> addSubscribersToPlace(@PathVariable Long placeId, @RequestBody Set<Long> subscribers, Authentication authentication) {
    return placeRepository.findById(placeId).map((val) -> {
      if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
        val.setSubscribers(Sets.newHashSet(userRepository.findAllById(subscribers)));
        return ResponseEntity.ok().body(placeRepository.save(val));
      }
      return ResponseEntity.badRequest().body("Access denied");
    }).orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
  @GetMapping("/get/all/{placeId}")
  @ApiOperation("Получение списка всех подписчиков заведения")
  ResponseEntity<?> getAllPlaceSubscribers(@PathVariable Long placeId, Authentication authentication) {
    return placeRepository.findById(placeId).map((val) -> {
      if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
        return ResponseEntity.ok().body(val.getSubscribers());
      }
      return ResponseEntity.badRequest().body("Access denied");
    }).orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
  @PostMapping("/notify/{placeId}")
  public ResponseEntity<?> notifyPlaceSubscribers(@PathVariable Long placeId,
                                                  @RequestParam(name = "mode", defaultValue = "SUBSCRIBERS", required = false) EPlaceNotifyMode placeNotifyMode,
                                                  @RequestBody Map<String, String> messageMap, Authentication authentication) {
    return placeRepository.findById(placeId).map((place) -> {
      if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(place, authentication)) {
        Set<String> subscribersFCMTokens = getTokens(place, placeNotifyMode);
        if (!subscribersFCMTokens.isEmpty()) {
          try {
            pushNotificationService.sendSubscribeMessage(subscribersFCMTokens, messageMap.get("title"), messageMap.get("message"));
          } catch (FirebaseMessagingException e) {
            return ResponseEntity.internalServerError().body("Firebase Messaging Error Code: " + e.getErrorCode());
          }
          return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body("Access denied");
    }).orElse(ResponseEntity.notFound().build());
  }

  private Set<String> getTokens(PlaceEntity place, EPlaceNotifyMode placeNotifyMode) {
    Set<String> tokens = new HashSet<>();
    switch (placeNotifyMode) {
      case SUBSCRIBERS -> tokens.addAll(getSubscribers(place));
      case STAFF -> tokens.addAll(getStaff(place));
      case ALL -> {
        tokens.addAll(getSubscribers(place));
        tokens.addAll(getStaff(place));
      }
    }
    return tokens;
  }

  private Set<String> getSubscribers(PlaceEntity place) {
    return place.getSubscribers().stream()
        .map(UserEntity::getFcmToken).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  private Set<String> getStaff(PlaceEntity place) {
    Set<String> staffSet = place.getStaff().stream()
        .map(UserEntity::getFcmToken).filter(Objects::nonNull).collect(Collectors.toSet());
    if (place.getOwner() != null && place.getOwner().getFcmToken() != null) {
      staffSet.add(place.getOwner().getFcmToken());
    }
    return staffSet;
  }
}
