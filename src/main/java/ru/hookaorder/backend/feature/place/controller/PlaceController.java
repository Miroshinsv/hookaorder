package ru.hookaorder.backend.feature.place.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;

import java.util.List;

@RestController
@RequestMapping(value = "/place")
@Api(description = "Контроллер заведения")
public class PlaceController {
    @Autowired
    private PlaceRepository placeRepository;

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

    @DeleteMapping("/disband/{id}")
    @ApiOperation("Помечаем заведение на удаление")
    @Where(clause = "deleted_at IS NULL")
    @SQLDelete(sql = "UPDATE places set deleted_at = now()::timestamp where id=?")
    ResponseEntity disbandById(@PathVariable Long id) {
        placeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Создаем заведение")
    ResponseEntity<PlaceEntity> createPlace(@RequestBody PlaceEntity placeEntity) {
        return ResponseEntity.ok(placeRepository.save(placeEntity));
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновляем заведение по id")
    ResponseEntity<PlaceEntity> updatePlace(@PathVariable Long id) {
        return placeRepository.findById(id).map((val) -> ResponseEntity.ok(placeRepository.save(val)))
                .orElse(ResponseEntity.badRequest().build());
    }
}
