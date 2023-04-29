package ru.hookaorder.backend.utils;

import com.google.firebase.messaging.Message;
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
            .build();
    }
}
