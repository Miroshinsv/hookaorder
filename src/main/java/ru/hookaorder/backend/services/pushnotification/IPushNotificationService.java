package ru.hookaorder.backend.services.pushnotification;

import com.google.firebase.messaging.BatchResponse;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.Set;

public interface IPushNotificationService {
    String sendNotificationChangeOderStatusUser(UserEntity User, OrderEntity order, EOrderStatus status);

    BatchResponse sendNotificationNewOrderToStuff(OrderEntity order, Set<String> FMCTokens);

}
