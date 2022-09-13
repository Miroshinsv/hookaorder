package ru.hookaorder.backend.feature.place.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.utils.CheckOwnerAndRolesAccess;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

import java.util.List;

@RestController
@RequestMapping(value = "/place")
@Api(description = "Контроллер заведения")
@AllArgsConstructor
public class PlaceController {
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @GetMapping("/get/{id}")
    @ApiOperation("Получение заведения по id")
    ResponseEntity<PlaceEntity> getPlaceById(@PathVariable Long id) {
        return placeRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Where(clause = "deleted_at IS NULL")
    @GetMapping("/get/all")
    @ApiOperation("Получение списка всех заведений")
    ResponseEntity<List<PlaceEntity>> getAllPlaces() {
        return ResponseEntity.ok(placeRepository.findAll());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    @ApiOperation("Создаем заведение")
    ResponseEntity<PlaceEntity> createPlace(@RequestBody PlaceEntity placeEntity, Authentication authentication) {
        if (!authentication.getAuthorities().contains(ERole.ADMIN)) {
            var user = userRepository.findById((Long) authentication.getPrincipal()).get();
            placeEntity.setOwner(user);
        }
        return ResponseEntity.ok(placeRepository.save(placeEntity));
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновляем заведение по id")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    ResponseEntity<?> updatePlace(@PathVariable Long id, @RequestBody PlaceEntity placeEntity, Authentication authentication) {
        return placeRepository.findById(id).map((val) -> {
            if (!CheckOwnerAndRolesAccess.isOwnerOrAdmin(placeEntity, authentication)) {
                return ResponseEntity.badRequest().body("Access denied");
            }
            NullAwareBeanUtilsBean.copyNoNullProperties(placeEntity, val);
            return ResponseEntity.ok(placeRepository.save(val));
        }).orElse(ResponseEntity.badRequest().body("Invalid place id"));
    }

    @DeleteMapping("/disband/{id}")
    @ApiOperation("Удаляем заведение")
    @SQLDelete(sql = "UPDATE places set deleted_at = now()::timestamp where id=?")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> disbandById(@PathVariable Long id, Authentication authentication) {
        return placeRepository.findById(id).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                placeRepository.deleteById(val.getId());
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.badRequest().body("Place not found"));
    }
}
