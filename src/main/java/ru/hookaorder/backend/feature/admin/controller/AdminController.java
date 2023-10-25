package ru.hookaorder.backend.feature.admin.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.order.service.OrderService;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.exception.PlaceNotCreatedException;
import ru.hookaorder.backend.feature.place.service.PlaceService;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.service.UserService;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
@RequestMapping(value = "/admin")
@AllArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final PlaceService placeService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/ping")
    ResponseEntity pingAdmin() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/place/create")
    @ApiOperation("Админ: Создаем заведение")
    ResponseEntity<PlaceEntity> createPlace(@RequestBody PlaceEntity placeEntity, Authentication authentication) {
        return placeService.create(placeEntity, authentication)
            .map(place -> ResponseEntity.ok(place))
            .orElseThrow(() -> new PlaceNotCreatedException("couldn't create place for request body."));
    }

    @PostMapping("/place/update/{id}")
    @ApiOperation("Админ: Обновляем заведение по id")
    ResponseEntity<?> updatePlace(@PathVariable Long id, @RequestBody PlaceEntity placeEntity,
                                  Authentication authentication) {
        return placeService.update(id, placeEntity, authentication)
            .map(place -> ResponseEntity.ok(place))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/place/disband/{id}")
    @ApiOperation("Админ: Удаляем заведение")
    ResponseEntity disbandPlaceById(@PathVariable Long id) {
        return placeService.delete(id)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @PostMapping("/order/create")
    @ApiOperation("Админ: Создаем заказ")
    ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity orderEntity, Authentication authentication) {
        return orderService.create(orderEntity, authentication, false)
            .map(order -> ResponseEntity.ok(order))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/order/update/{id}")
    @ApiOperation("Админ: Обновляем заказ по id")
    ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderEntity orderEntity,
                                  Authentication authentication) {
        return orderService.update(id, orderEntity, authentication)
            .map(order -> ResponseEntity.ok(order))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/order/disband/{id}")
    @ApiOperation("Админ: Удаляем заказ")
    ResponseEntity disbandOrderById(@PathVariable Long id) {
        return orderService.delete(id)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/user/create")
    @ApiOperation("Админ: Создание пользователя")
    ResponseEntity<?> createUser(@RequestBody UserEntity userEntity, Authentication authentication) {
        return userService.create(userEntity)
            .map(user -> ResponseEntity.ok(user))
            .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/user/update/{id}")
    @ApiOperation("Админ: Обновление пользователя по id")
    ResponseEntity<?> updateUserById(@PathVariable Long id, @RequestBody UserEntity userEntity, Authentication authentication) {
        return userService.update(id, userEntity, authentication)
            .map(user -> ResponseEntity.ok(user))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user/disband/{id}")
    @ApiOperation("Админ: Удаляем пользователя по id")
    ResponseEntity<?> disbandUserById(@PathVariable Long id) {
        return userService.delete(id)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }
}
