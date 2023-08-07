package ru.hookaorder.backend.feature.place.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
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

    @Where(clause = "deleted_at IS NULL")
    @GetMapping("/get/{id}")
    @ApiOperation("Получение заведения по id")
    ResponseEntity<?> getPlaceById(@PathVariable Long id, Authentication authentication) {
        return placeRepository.findById(id)
            .map(place -> JsonUtils.checkAndApplyPhoneFilter(place, authentication))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Where(clause = "deleted_at IS NULL")
    @GetMapping("/get/assigned")
    @ApiOperation("Получение моих мест")
    ResponseEntity<List<PlaceEntity>> getMyPlaces(Authentication authentication) {
        return ResponseEntity.ok(placeRepository
                .findAll()
                .stream()
                .filter((val) -> {
                    List<Long> staffIDs = val.getStaff()
                            .stream().map(BaseEntity::getId).toList();
                    return val.getOwner().getId().equals(authentication.getPrincipal()) ||
                            staffIDs.contains(authentication.getPrincipal());
                })
                .collect(Collectors.toList()));
    }

    @Where(clause = "deleted_at IS NULL")
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
        return ResponseEntity.ok(placeService.create(placeEntity, authentication));
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновляем заведение по id")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    ResponseEntity<?> updatePlace(@PathVariable Long id, @RequestBody PlaceEntity placeEntity, Authentication authentication) {
        return placeService.update(id, placeEntity, authentication)
            .map(place -> ResponseEntity.ok(place))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/disband/{id}")
    @ApiOperation("Удаляем заведение")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> disbandById(@PathVariable Long id, Authentication authentication) {
        return placeService.delete(id)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @GetMapping("/ratings/{placeId}")
    public ResponseEntity<?> getPlaceRatings(@PathVariable Long placeId, Authentication authentication) {
        return placeRepository.findById(placeId).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                return ResponseEntity.ok().body(val.getRatings());
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.notFound().build());
    }
}