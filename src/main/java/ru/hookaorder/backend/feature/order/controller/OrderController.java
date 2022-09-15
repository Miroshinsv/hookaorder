package ru.hookaorder.backend.feature.order.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.order.repository.OrderRepository;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

@RestController
@RequestMapping(value = "/order")
@Api(description = "Контроллер заказов")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @GetMapping("/get/{id}")
    @ApiOperation("Получение заказа по id")
    ResponseEntity<?> getOrderById(@PathVariable Long id, Authentication authentication) {
        return orderRepository.findById(id).map((val) -> {
            if (val.getUserId().getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN) || val.getPlaceId().getOwner().equals(authentication.getPrincipal())) {
                return ResponseEntity.ok().body(val);
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get/all/{currentPlaceId}")
    @ApiOperation("Получение всех заказов")
    ResponseEntity<?> getAllOrders(@PathVariable Long currentPlaceId, Authentication authentication) {
        var roles = authentication.getAuthorities();
        if (roles.contains(ERole.ADMIN)) {
            return ResponseEntity.ok().body(orderRepository.findAllByPlaceId(placeRepository.findById(currentPlaceId).get()));
        } else if (roles.contains(ERole.OWNER)) {
            if (placeRepository.findById(currentPlaceId).get().getOwner().getId().equals(authentication.getPrincipal())) {
                return ResponseEntity.ok().body(orderRepository.findAllByPlaceId(placeRepository.findById(currentPlaceId).get()));
            }
            return ResponseEntity.badRequest().body("invalid place id");
        } else if (roles.contains(ERole.HOOKAH_MASTER) || roles.contains(ERole.WAITER)) {
            var place = userRepository.findById((Long) authentication.getPrincipal()).get().getWorkPlaces().stream().filter((val) -> val.getId().equals(currentPlaceId)).findFirst().orElseThrow();
            return ResponseEntity.ok().body(orderRepository.findAllByPlaceId(place));
        } else {
            return ResponseEntity.ok().body(orderRepository.findAllByPlaceIdAndUserId(placeRepository.findById(currentPlaceId).get(), userRepository.findById((Long) authentication.getPrincipal()).get()));
        }
    }

    @PostMapping("/create")
    @ApiOperation("Создание заказа")
    ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity orderEntity) {
        return ResponseEntity.ok(orderRepository.save(orderEntity));
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновление заказа по id")
    ResponseEntity<?> updateOrder(@PathVariable Long id, Authentication authentication) {
        return orderRepository.findById(id).map((val) -> {
            if (val.getUserId().getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN)) {
                NullAwareBeanUtilsBean.copyNoNullProperties(orderRepository.findById(id), val);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.badRequest().build());
    }
}
