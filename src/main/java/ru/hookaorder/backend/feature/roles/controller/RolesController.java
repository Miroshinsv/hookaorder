package ru.hookaorder.backend.feature.roles.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.roles.entity.RoleEntity;
import ru.hookaorder.backend.feature.roles.entity.UpdateRoleRequest;
import ru.hookaorder.backend.feature.roles.repository.RoleRepository;
import ru.hookaorder.backend.feature.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@RequestMapping("/roles")
@RestController
@AllArgsConstructor
public class RolesController {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    @PostMapping("/create")
    ResponseEntity createRole(@RequestBody RoleEntity role) {
        role.setRoleName(role.getRoleName().toUpperCase(Locale.ROOT).trim());
        if (role.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(roleRepository.save(role));
    }

    @GetMapping("/get/all")
    ResponseEntity getAllRoles() {
        return ResponseEntity.ok().body(roleRepository.findAll());
    }

    @PostMapping("/set.role/")
    ResponseEntity setRoleToUser(@RequestBody UpdateRoleRequest updateRoleRequest) {
        var user = userRepository.findById(updateRoleRequest.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Set<RoleEntity> roles = new HashSet<>();
        updateRoleRequest.getRoleIds().forEach((roleId) -> {
            var tmp = roleRepository.findById(roleId);
            if (tmp.isEmpty()) throw new EntityNotFoundException();
            roles.add(tmp.get());
        });
        user.get().setRolesSet(roles);
        return ResponseEntity.ok().body(userRepository.save(user.get()));
    }
}
