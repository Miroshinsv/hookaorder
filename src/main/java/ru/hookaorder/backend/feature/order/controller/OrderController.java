package ru.hookaorder.backend.feature.order.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.order.exception.OrderAccessDeniedException;
import ru.hookaorder.backend.feature.order.exception.OrderInvalidStatusException;
import ru.hookaorder.backend.feature.order.exception.OrderNotFoundException;
import ru.hookaorder.backend.feature.order.repository.OrderRepository;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.exception.PlaceAccessDeniedException;
import ru.hookaorder.backend.feature.place.exception.PlaceNotFoundException;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.exception.UserNotFoundException;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.services.pushnotification.IPushNotificationService;
import ru.hookaorder.backend.utils.JsonUtils;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/order")
@Api(tags = "Контроллер заказов")
@RequiredArgsConstructor
public class OrderController {

    private final static int FIRST_PAGE = 0;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final IPushNotificationService pushNotificationService;

    @GetMapping("/get/{id}")
    @ApiOperation("Получение заказа по id")
    ResponseEntity<?> getOrderById(@PathVariable Long id, Authentication authentication) {
        return orderRepository.findById(id).map((val) -> {
            if (isOrderOwnedByUser(val, authentication) ||
                authentication.getAuthorities().contains(ERole.ADMIN) ||
                val.getPlaceId().getOwner().equals(authentication.getPrincipal())) {
                return ResponseEntity.ok().body(JsonUtils.checkAndApplyPhoneFilter(val, authentication));
            }
            throw new OrderAccessDeniedException("user doesn't have ADMIN or order place OWNER permissions and doesn't owns the order with ID: " + id);
        }).orElseThrow(() -> new OrderNotFoundException("by order ID " + id));
    }

    @GetMapping("/get/my")
    @ApiOperation("Получение собственных заказов")
    ResponseEntity<?> getMyOrders(@RequestParam(name = "status", defaultValue = "") String status, @RequestParam(name = "count", defaultValue = "0") Integer count, Authentication authentication) {
        UserEntity user = userRepository.findById((Long) authentication.getPrincipal()).orElseThrow();
        Pageable filterOrdersPageable = createPageRequest(count.equals(0) ? Integer.MAX_VALUE : count);
        if (status.equals("")) {
            return ResponseEntity.ok().body(JsonUtils.checkAndApplyPhoneFilterForList(orderRepository.findAllByUserId(user, filterOrdersPageable), authentication));
        } else {
            return ResponseEntity.ok().body(JsonUtils.checkAndApplyPhoneFilterForList(orderRepository.findAllByUserIdAndOrderStatus(user, EOrderStatus.valueOf(status), filterOrdersPageable), authentication));
        }
    }

    private Pageable createPageRequest(Integer rowsCount) {
        return PageRequest.of(FIRST_PAGE, rowsCount, Sort.by("createdAt").descending());
    }

    @GetMapping("/get/all/{currentPlaceId}")
    @ApiOperation("Получение всех заказов по ID")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER','HOOKAH_MASTER','WAITER')")
    ResponseEntity<?> getAllOrders(@PathVariable Long currentPlaceId, @RequestParam(name = "new", defaultValue = "false") Boolean newOnly, Authentication authentication) {
        var place = placeRepository.findById(currentPlaceId)
            .orElseThrow(() -> new PlaceNotFoundException("by place ID " + currentPlaceId));

        var user = userRepository.findById((Long) authentication.getPrincipal()).get();

        // check is owner or stuff
        if ((place.getOwner() == null || !(place.getOwner().getId().equals(user.getId()))) &&
            place.getStaff().stream().noneMatch(val -> val.getId().equals(user.getId())) &&
            !authentication.getAuthorities().contains(ERole.ADMIN)) {
            throw new PlaceAccessDeniedException("user don't have OWNER or ADMIN permissions and not a member of Staff for place ID " + currentPlaceId);
        }

        // filter by new orders
        List<OrderEntity> orders;
        if (newOnly) {
            orders = orderRepository.findAllByPlaceIdAndAndOrderStatus(place, EOrderStatus.NEW.name());
        } else {
            orders = orderRepository.findAllByPlaceId(place);
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    @ApiOperation("Создание заказа")
    ResponseEntity<?> createOrder(@RequestBody OrderEntity orderEntity, Authentication authentication) {
        Long placeId = orderEntity.getPlaceId().getId();
        PlaceEntity place = placeRepository.findById(placeId)
            .orElseThrow(() -> new PlaceNotFoundException("by place ID " + placeId));

        Long userId = (Long) authentication.getPrincipal();
        UserEntity userOrdered = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("by user ID " + userId));

        orderEntity.setPlaceId(place);
        orderEntity.setUserId(userOrdered);
        orderEntity.setOrderStatus(EOrderStatus.NEW);
        orderRepository.save(orderEntity);

        Set<String> userFMCTokenList = place.getStaff().stream().map(UserEntity::getFcmToken).filter(Objects::nonNull).collect(Collectors.toSet());

        if (place.getOwner() != null && place.getOwner().getFcmToken() != null) {
            userFMCTokenList.add(place.getOwner().getFcmToken());
        }
        pushNotificationService.sendNotificationNewOrderToStaff(orderEntity, userFMCTokenList);
        return ResponseEntity.ok(JsonUtils.checkAndApplyPhoneFilter(orderEntity, authentication));
    }

    @PostMapping("/update/{id}")
    @ApiOperation("Обновление заказа по id")
    ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderEntity orderEntity, Authentication authentication) {
        return orderRepository.findById(id).map((val) -> {
            if (isOrderOwnedByUser(val, authentication) || authentication.getAuthorities().contains(ERole.ADMIN)) {
                NullAwareBeanUtilsBean.copyNoNullProperties(orderEntity, val);
                return ResponseEntity.ok().body(JsonUtils.checkAndApplyPhoneFilter(orderRepository.save(val), authentication));
            }
            throw new OrderAccessDeniedException("user doesn't have ADMIN permission and doesn't owns the order with ID: " + id);
        }).orElseThrow(() -> new OrderNotFoundException("by order ID " + id));
    }

    @PostMapping("/taken/{id}")
    @ApiOperation("Взятие заказа в работу по id")
    ResponseEntity<?> takeOrder(@PathVariable Long id, Authentication authentication) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.get().getOrderStatus() != EOrderStatus.NEW) {
            throw new OrderInvalidStatusException(
                "just order with NEW status could be TAKEN in progress. " +
                    "Order ID: " + id +
                    ". Order Status: " + optionalOrder.get().getOrderStatus());
        }
        return optionalOrder.map((val) -> {
            if (isOrderOwnedByUser(val, authentication) || authentication.getAuthorities().contains(ERole.ADMIN) || isOrderProcessedByExecutor(val, authentication)) {
                val.setTakenAt(LocalDate.now());
                val.setOrderStatus(EOrderStatus.TAKEN);
                pushNotificationService.sendNotificationChangeOrderStatusUser(val.getUserId(), val, EOrderStatus.TAKEN);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            throw new OrderAccessDeniedException("user doesn't have ADMIN permission and doesn't owns or process the order with ID: " + id);
        }).orElseThrow(() -> new OrderNotFoundException("by order ID " + id));
    }

    @PostMapping("/complete/{id}")
    @ApiOperation("Закрытие заказа по id")
    ResponseEntity<?> completeOrder(@PathVariable Long id, Authentication authentication) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.get().getOrderStatus() != EOrderStatus.TAKEN) {
            throw new OrderInvalidStatusException(
                "just order with TAKEN status could be COMPLETED. " +
                    "Order ID: " + id +
                    ". Order Status: " + optionalOrder.get().getOrderStatus());
        }
        return optionalOrder.map((val) -> {
            if (isOrderOwnedByUser(val, authentication) || authentication.getAuthorities().contains(ERole.ADMIN) || isOrderProcessedByExecutor(val, authentication)) {
                val.setCompletedAt(LocalDate.now());
                val.setOrderStatus(EOrderStatus.COMPLETED);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }
            throw new OrderAccessDeniedException("user doesn't have ADMIN permission and doesn't owns or process the order with ID: " + id);
        }).orElseThrow(() -> new OrderNotFoundException("by order ID " + id));
    }

    @PostMapping("/cancel/{id}")
    @ApiOperation("Отмена заказа по id")
    ResponseEntity<?> cancelOrder(@PathVariable Long id, Authentication authentication) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.get().getOrderStatus() == EOrderStatus.COMPLETED) {
            throw new OrderInvalidStatusException(
                "COMPLETED orders couldn't be CANCELLED. " +
                    "Order ID: " + id +
                    ". Order Status: " + optionalOrder.get().getOrderStatus());
        }
        return optionalOrder.map((val) -> {
            if (isOrderOwnedByUser(val, authentication) || authentication.getAuthorities().contains(ERole.ADMIN) || isOrderProcessedByExecutor(val, authentication)) {
                val.setCancelledAt(LocalDate.now());
                val.setOrderStatus(EOrderStatus.CANCELLED);
                pushNotificationService.sendNotificationChangeOrderStatusUser(val.getUserId(), val, EOrderStatus.CANCELLED);
                return ResponseEntity.ok().body(orderRepository.save(val));
            }

            throw new OrderAccessDeniedException("user doesn't have ADMIN permission and doesn't owns or process the order with ID: " + id);
        }).orElseThrow(() -> new OrderNotFoundException("by order ID " + id));
    }

    private boolean isOrderProcessedByExecutor(OrderEntity order, Authentication authentication) {
        var roles = authentication.getAuthorities();
        if (roles.contains(ERole.HOOKAH_MASTER) || roles.contains(ERole.WAITER)) {
            return userRepository.findById((Long) authentication.getPrincipal()).get().getWorkPlaces().stream().filter((place) -> place.equals(order.getPlaceId())).count() > 0;
        }
        return false;
    }

    private boolean isOrderOwnedByUser(OrderEntity order, Authentication authentication) {
        return order.getUserId().getId().equals(authentication.getPrincipal());
    }
}
