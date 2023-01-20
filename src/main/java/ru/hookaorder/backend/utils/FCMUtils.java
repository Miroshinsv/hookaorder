package ru.hookaorder.backend.utils;

import ru.hookaorder.backend.feature.order.entity.OrderEntity;

public final class FCMUtils {

    private FCMUtils() {
    }

    public static String getOrderMsgText(OrderEntity orderEntity) {
        return new StringBuilder("От: ")
                .append(orderEntity.getUserId() != null
                        ? orderEntity.getUserId().getPhone()
                        : "")
                .append(". На ")
                .append(orderEntity.getOrderTime())
                .append(". ")
                .append(orderEntity.getComment())
                .toString();
    }
}
