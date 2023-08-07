package ru.hookaorder.backend.feature.order.service;

import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.order.repository.OrderRepository;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.place.repository.PlaceRepository;
import ru.hookaorder.backend.feature.roles.entity.ERole;
import ru.hookaorder.backend.feature.user.entity.UserEntity;
import ru.hookaorder.backend.feature.user.repository.UserRepository;
import ru.hookaorder.backend.services.pushnotification.IPushNotificationService;
import ru.hookaorder.backend.utils.CheckOwnerAndRolesAccess;
import ru.hookaorder.backend.utils.NullAwareBeanUtilsBean;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Optional<OrderEntity> EMPTY_ORDER = Optional.empty();
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final OrderRepository orderRepository;

    private final IPushNotificationService pushNotificationService;

    @Override
    public Optional<OrderEntity> create(OrderEntity orderEntity, Authentication authentication, boolean notify) {
        Optional<PlaceEntity> placeOptional = placeRepository.findById(orderEntity.getPlaceId().getId());
        Optional<UserEntity> userOrdered = userRepository.findById((Long) authentication.getPrincipal());
        if (!(placeOptional.isPresent() && userOrdered.isPresent())) {
            return EMPTY_ORDER;
        }

        PlaceEntity place = placeOptional.get();
        orderEntity.setPlaceId(place);
        orderEntity.setUserId(userOrdered.get());
        orderEntity.setOrderStatus(EOrderStatus.NEW);
        orderRepository.save(orderEntity);

        if (notify) {
            Set<String> userFMCTokenList = place.getStaff().stream().map(UserEntity::getFcmToken).filter(Objects::nonNull).collect(Collectors.toSet());

            if (place.getOwner() != null && place.getOwner().getFcmToken() != null) {
                userFMCTokenList.add(place.getOwner().getFcmToken());
            }
            pushNotificationService.sendNotificationNewOrderToStaff(orderEntity, userFMCTokenList);
        }

        return Optional.of(orderEntity);
    }

    @Where(clause = "deleted_at IS NULL")
    @Override
    public Optional<OrderEntity> update(Long id, OrderEntity orderEntity, Authentication authentication) {
        return orderRepository.findById(id).map((orderToUpdate) -> {
            if (!(CheckOwnerAndRolesAccess.isOrderOwnedByUser(orderToUpdate, authentication)
                || authentication.getAuthorities().contains(ERole.ADMIN))) {
                return EMPTY_ORDER;
            }
            NullAwareBeanUtilsBean.copyNoNullProperties(orderEntity, orderToUpdate);
            return Optional.of(orderRepository.save(orderToUpdate));
        }).orElse(EMPTY_ORDER);
    }

    @Where(clause = "deleted_at IS NULL")
    @SQLDelete(sql = "UPDATE orders set deleted_at = now()::timestamp where id=?")
    @Override
    public boolean delete(Long id) {
        return orderRepository.findById(id).map((order) -> {
            orderRepository.deleteById(order.getId());
            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }
}
