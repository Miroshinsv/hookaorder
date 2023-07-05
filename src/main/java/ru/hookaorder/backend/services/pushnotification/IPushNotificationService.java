package ru.hookaorder.backend.services.pushnotification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.Set;

public interface IPushNotificationService {
    String sendNotificationChangeOrderStatusUser(UserEntity User, OrderEntity order, EOrderStatus status);

    BatchResponse sendSubscribeMessage(Set<String> FCMTokens, String title, String message) throws FirebaseMessagingException;

    BatchResponse sendNotificationNewOrderToStaff(OrderEntity order, Set<String> FCMTokens);

}
