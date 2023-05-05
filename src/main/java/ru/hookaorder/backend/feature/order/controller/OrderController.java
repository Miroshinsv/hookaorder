package ru.hookaorder.backend.feature.order.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.order.repository.OrderRepository;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.services.pushnotification.IPushNotificationService;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/order")
@Api(tags = "Контроллер заказов")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final IPushNotificationService pushNotificationService;

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
    ResponseEntity<?> getAllOrders(@PathVariable Long currentPlaceId, @RequestParam(name = "new", defaultValue = "false") Boolean newOnly, Authentication authentication) {
        var roles = authentication.getAuthorities();
        if (roles.contains(ERole.ADMIN)) {
            if (newOnly) {
                return ResponseEntity.ok().body(orderRepository.findNewByPlaceId(placeRepository.findById(currentPlaceId).get()));
            }
            return ResponseEntity.ok().body(orderRepository.findAllByPlaceId(placeRepository.findById(currentPlaceId).get()));
        } else if (roles.contains(ERole.OWNER)) {
            if (placeRepository.findById(currentPlaceId).get().getOwner().getId().equals(authentication.getPrincipal())) {
                if (newOnly) {
                    return ResponseEntity.ok().body(orderRepository.findNewByPlaceId(placeRepository.findById(currentPlaceId).get()));
                }
                return ResponseEntity.ok().body(orderRepository.findAllByPlaceId(placeRepository.findById(currentPlaceId).get()));
            }
            return ResponseEntity.badRequest().body("invalid place id");
        } else if (roles.contains(ERole.HOOKAH_MASTER) || roles.contains(ERole.WAITER)) {
            var place = userRepository.findById((Long) authentication.getPrincipal()).get().getWorkPlaces().stream().filter((val) -> val.getId().equals(currentPlaceId)).findFirst().orElseThrow();
            if (newOnly) {
                return ResponseEntity.ok().body(orderRepository.findNewByPlaceId(place));
            }
            return ResponseEntity.ok().body(orderRepository.findAllByPlaceId(place));
        } else {
            if (newOnly) {
                return ResponseEntity.ok().body(orderRepository.findNewByPlaceIdAndUserId(placeRepository.findById(currentPlaceId).get(), userRepository.findById((Long) authentication.getPrincipal()).get()));
            }
            return ResponseEntity.ok().body(orderRepository.findAllByPlaceIdAndUserId(placeRepository.findById(currentPlaceId).get(), userRepository.findById((Long) authentication.getPrincipal()).get()));
        }
    }

    @PostMapping("/create")
    @ApiOperation("Создание заказа")
    ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity orderEntity) {
        PlaceEntity place = placeRepository.findById(orderEntity.getPlaceId().getId()).orElseThrow();

        orderEntity.setOrderStatus(EOrderStatus.NEW);
        orderRepository.save(orderEntity);

        List<String> userFMCTokenList = place.getStaff().stream().map(UserEntity::getFcmToken).filter(Objects::nonNull).collect(Collectors.toList());

        if (place.getOwner() != null && place.getOwner().getFcmToken() != null) {
            userFMCTokenList.add(place.getOwner().getFcmToken());
        }
        pushNotificationService.sendNotificationNewOrderToStuff(orderEntity, userFMCTokenList);
        return ResponseEntity.ok(orderEntity);
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновление заказа по id")
    ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderEntity orderEntity, Authentication authentication) {
        return orderRepository.findById(id).map((val) -> {
            if (val.getUserId().getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN)) {
                NullAwareBeanUtilsBean.copyNoNullProperties(orderEntity, val);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/taken/{id}")
    @ApiOperation("Взятие заказа в работу по id")
    ResponseEntity<?> takeOrder(@PathVariable Long id, Authentication authentication) {
        if (orderRepository.findById(id).get().getOrderStatus() != EOrderStatus.NEW) {
            return ResponseEntity.badRequest().body("Just orders with NEW status could be taken in progress");
        }
        return orderRepository.findById(id).map((val) -> {
            if (val.getUserId().getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN) || isOrderProcessedByExecutor(val, authentication)) {
                val.setTakenAt(LocalDate.now());
                val.setOrderStatus(EOrderStatus.TAKEN);
                pushNotificationService.sendNotificationChangeOderStatusUser(val.getUserId(), val, EOrderStatus.TAKEN);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/complete/{id}")
    @ApiOperation("Закрытие заказа по id")
    ResponseEntity<?> completeOrder(@PathVariable Long id, Authentication authentication) {
        if (orderRepository.findById(id).get().getOrderStatus() != EOrderStatus.TAKEN) {
            return ResponseEntity.badRequest().body("Just orders with TAKEN status could be completed");
        }
        return orderRepository.findById(id).map((val) -> {
            if (val.getUserId().getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN) || isOrderProcessedByExecutor(val, authentication)) {
                val.setCompletedAt(LocalDate.now());
                val.setOrderStatus(EOrderStatus.COMPLETED);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/cancel/{id}")
    @ApiOperation("Отмена заказа по id")
    ResponseEntity<?> cancelOrder(@PathVariable Long id, Authentication authentication) {
        if (orderRepository.findById(id).get().getOrderStatus() == EOrderStatus.COMPLETED) {
            return ResponseEntity.badRequest().body("COMPLETED orders couldn't be cancelled");
        }
        return orderRepository.findById(id).map((val) -> {
            if (val.getUserId().getId().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(ERole.ADMIN)) {
                val.setCancelledAt(LocalDate.now());
                val.setOrderStatus(EOrderStatus.CANCELLED);
                pushNotificationService.sendNotificationChangeOderStatusUser(val.getUserId(), val, EOrderStatus.CANCELLED);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            return ResponseEntity.badRequest().body("Access denied");
        }).orElse(ResponseEntity.badRequest().build());
    }

    private boolean isOrderProcessedByExecutor(OrderEntity order, Authentication authentication) {
        var roles = authentication.getAuthorities();
        if (roles.contains(ERole.HOOKAH_MASTER) || roles.contains(ERole.WAITER)) {
            return userRepository.findById((Long) authentication.getPrincipal()).get().getWorkPlaces().stream().filter((place) -> place.equals(order.getPlaceId())).count() > 0;
        }
        return false;
    }
}
