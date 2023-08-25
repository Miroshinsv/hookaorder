package ru.hookaorder.backend.feature.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.feature.user.service.UserService;
import ru.hookaorder.backend.utils.JsonUtils;

@RestController
@RequestMapping(value = "/user")
@Api(tags = "Контроллер пользователей")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping(value = "/get/{id}")
    @ApiOperation("Получение пользователя по id")
    ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
        return userRepository.findById(id)
            .map((val) -> ResponseEntity.ok().body(JsonUtils.checkAndApplyPhoneFilter(val, authentication)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    @ApiOperation("Создание пользователя")
    ResponseEntity<?> createUser(@RequestBody UserEntity userEntity) {
        return userService.create(userEntity)
            .map(user -> ResponseEntity.ok(user))
            .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/update/{id}")
    @ApiOperation("Обновление пользователя по id")
    ResponseEntity<?> updateUserById(@PathVariable Long id, @RequestBody UserEntity userEntity, Authentication authentication) {
        return userService.update(id, userEntity, authentication)
            .map(user -> ResponseEntity.ok(user))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/disable")
    @ApiOperation(value = "Деактивация аккаунта со стороны пользователя")
    ResponseEntity<?> disableByUser(Authentication authentication) {
        UserEntity entity = userRepository.findById((Long) authentication.getPrincipal()).get();
        entity.setEnabled(false);
        userRepository.save(entity);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/get/all")
    @ApiOperation("Получение списка всех пользователя")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "", hidden = true)
    ResponseEntity<?> disbandUserById(@PathVariable Long id) {
        return userService.delete(id)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }
}