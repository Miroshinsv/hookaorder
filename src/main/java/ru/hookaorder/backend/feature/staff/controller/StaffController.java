package ru.hookaorder.backend.feature.staff.controller;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.utils.CheckOwnerAndRolesAccess;

import java.util.Set;

@RequestMapping(value = "/place")
@RestController
@AllArgsConstructor
public class StaffController {
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @PostMapping("/staff/add/{placeId}")
    public ResponseEntity<?> addStaffToPlace(@PathVariable Long placeId, @RequestBody Set<Long> users, Authentication authentication) {
        return placeRepository.findById(placeId).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                val.setStaff(Sets.newHashSet(userRepository.findAllById(users)));
                return ResponseEntity.ok().body(placeRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @DeleteMapping("/staff/delete/{placeId}")
    public ResponseEntity<?> deleteStuffFromPlace(@PathVariable Long placeId, @RequestBody Set<Long> users, Authentication authentication) {
        return placeRepository.findById(placeId).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                val.getStaff().removeAll(Sets.newHashSet(userRepository.findAllById(users)));
                return ResponseEntity.ok().body(placeRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @GetMapping("{placeId}/ratings/{staffId}")
    public ResponseEntity<?> getStaffRatings(@PathVariable Long placeId, @PathVariable Long staffId, Authentication authentication) {
        return placeRepository.findById(placeId).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                UserEntity staffEntity = userRepository.findById(staffId).get();
                if (val.getStaff().contains(staffEntity)) {
                    return ResponseEntity.ok().body(staffEntity.getRatings());
                }
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @GetMapping("{placeId}/ratings/allStaff")
    public ResponseEntity<?> getAllStaffRatings(@PathVariable Long placeId, Authentication authentication) {
        return placeRepository.findById(placeId).map((val) -> {
            if (CheckOwnerAndRolesAccess.isOwnerOrAdmin(val, authentication)) {
                return ResponseEntity.ok().body(val.getStaff().stream().map(UserEntity::getRatings));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.notFound().build());
    }
}
