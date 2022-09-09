package ru.hookaorder.backend.feature.admin.controller;

import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;

@RestController
@RequestMapping(value = "/admin")
@AllArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final PlaceRepository placeRepository;

    @GetMapping("/ping")
    ResponseEntity pingAdmin() {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/disband/{id}")
    @Where(clause = "deleted_at IS NULL")
    @SQLDelete(sql = "UPDATE places set deleted_at = now()::timestamp where id=?")
    ResponseEntity disbandById(@PathVariable Long id) {
        placeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
