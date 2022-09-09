package ru.hookaorder.backend.feature.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping(value = "/user")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(value = "/get/{id}")
    ResponseEntity getUserById(@PathVariable Long id) {
        return userRepository
                .findById(id)
                .map((val) -> ResponseEntity.ok().body(val))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    ResponseEntity createUser(@RequestBody UserEntity user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        if (!Objects.isNull(user.getRolesSet())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity
                .ok()
                .body(userRepository.save(user));
    }

    @PutMapping(value = "/update/{id}")
    ResponseEntity updateUser(@PathVariable Long id, @RequestBody UserEntity user) {
        if (user.getRolesSet().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository
                .findById(id)
                .map((val) -> ResponseEntity.ok().body(userRepository.save(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/get/all")
    ResponseEntity getAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll());
    }
}