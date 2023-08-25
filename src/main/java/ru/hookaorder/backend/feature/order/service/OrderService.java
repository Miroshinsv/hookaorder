package ru.hookaorder.backend.feature.order.service;

import org.springframework.security.core.Authentication;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;

import java.util.Optional;

public interface OrderService {
    Optional<OrderEntity> create(OrderEntity place, Authentication authentication, boolean notify);
    Optional<OrderEntity> update(Long id, OrderEntity place, Authentication authentication);
    boolean delete(Long id);
}
