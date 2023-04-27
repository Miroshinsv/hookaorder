package ru.hookaorder.backend.utils;

import com.google.firebase.messaging.Message;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;

public final class FCMUtils {

    private FCMUtils() {
    }

    public static Message getOrderMsgText(OrderEntity orderEntity, String fmcToken) {
        return Message.builder()
                .setToken(fmcToken)
                .putData("От", orderEntity.getUserId() != null
                        ? orderEntity.getUserId().getPhone()
                        : "Телефон не указан")
                .putData("На", orderEntity.getOrderTime())
                .build();
    }
}
