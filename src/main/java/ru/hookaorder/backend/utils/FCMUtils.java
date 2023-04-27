package ru.hookaorder.backend.utils;

import ru.hookaorder.backend.feature.order.entity.OrderEntity;

public final class FCMUtils {

  private FCMUtils() {
  }

  public static String getOrderMsgText(OrderEntity orderEntity, String userPhone) {
    return new StringBuilder("От: ")
        .append(userPhone != null ? userPhone : "")
        .append(". На ")
        .append(orderEntity.getOrderTime())
        .append(". ")
        .append(orderEntity.getComment() != null ? orderEntity.getComment().getText() : "")
        .toString();
  }
}
