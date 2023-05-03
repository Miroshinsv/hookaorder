package ru.hookaorder.backend.utils;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;

public final class FCMUtils {

    private FCMUtils() {
    }

    public static Message getOrderMsgText(OrderEntity orderEntity, String userPhone, String fmcToken) {
        return Message.builder()
            .setToken(fmcToken)
            .putData("phone", userPhone)
            .putData("time", orderEntity.getOrderTime())
            .putData("comment", orderEntity.getComment() != null ? orderEntity.getComment().getText() : "")
            .setNotification(Notification.builder().setTitle("Новый заказ от " + userPhone).setBody("Комментарий: " + orderEntity.getComment().getText()).build())
            .build();
    }
}
