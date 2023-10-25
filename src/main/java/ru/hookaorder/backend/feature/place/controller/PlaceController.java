package ru.hookaorder.backend.feature.place.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.exception.PlaceAccessDeniedException;
import ru.hookaorder.backend.feature.place.exception.PlaceNotCreatedException;
import ru.hookaorder.backend.feature.place.exception.PlaceNotFoundException;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.place.service.PlaceService;
import ru.hookaorder.backend.utils.CheckOwnerAndRolesAccess;
import ru.hookaorder.backend.utils.JsonUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/place")
@Api(tags = "Контроллер заведения")
@AllArgsConstructor
public class PlaceController {
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;

    @GetMapping("/get/{id}")
    @ApiOperation("Получение заведения по id")
    ResponseEntity<?> getPlaceById(@PathVariable Long id, Authentication authentication) {
        return placeRepository.findById(id)
            .map(place -> JsonUtils.checkAndApplyPhoneFilter(place, authentication))
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new PlaceNotFoundException("by place ID " + id));
    }

    @GetMapping("/get/assigned")
    @ApiOperation("Получение моих мест")
    ResponseEntity<List<PlaceEntity>> getMyPlaces(Authentication authentication) {
        return ResponseEntity.ok(
            placeRepository.findAll()
                .stream()
                .filter((place) -> {
                    List<Long> staffIDs = place.getStaff().stream().map(BaseEntity::getId).toList();
                    return (place.getOwner() != null && place.getOwner().getId().equals(authentication.getPrincipal()))
                        || staffIDs.contains(authentication.getPrincipal());
                }).collect(Collectors.toList()));
    }

    @GetMapping("/get/all")
    @ApiOperation("Получение списка всех заведений")
    ResponseEntity<?> getAllPlaces(Authentication authentication) {
        return ResponseEntity.ok(
            JsonUtils.checkAndApplyPhoneFilterForList(placeRepository.findAll(), authentication));
    }

    @PostMapping("/create")
    @ApiOperation("Создаем заведение")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    ResponseEntity<PlaceEntity> createPlace(@RequestBody PlaceEntity placeEntity, Authentication authentication) {
        return placeService.create(placeEntity, authentication)
            .map(place -> ResponseEntity.ok(place))
            .orElseThrow(() -> new PlaceNotCreatedException("couldn't create place for request body."));
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновляем заведение по id")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    ResponseEntity<?> updatePlace(@PathVariable Long id, @RequestBody PlaceEntity placeEntity, Authentication authentication) {
        return placeService.update(id, placeEntity, authentication)
            .map(place -> ResponseEntity.ok(place))
            .orElseThrow(() -> new PlaceNotFoundException("by place ID " + id));
    }

    @DeleteMapping("/disband/{id}")
    @ApiOperation("Удаляем заведение")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> disbandById(@PathVariable Long id, Authentication authentication) {
        if (!placeService.delete(id)) {
            throw new PlaceNotFoundException("by place ID " + id);
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @GetMapping("/ratings/{placeId}")
    public ResponseEntity<?> getPlaceRatings(@PathVariable Long placeId, Authentication authentication) {
        return placeRepository.findById(placeId).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                return ResponseEntity.ok().body(val.getRatings());
            }
            throw new PlaceAccessDeniedException("user don't have OWNER or ADMIN permissions for place ID " + placeId);
        }).orElseThrow(() -> new PlaceNotFoundException("by place ID " + placeId));
    }
}