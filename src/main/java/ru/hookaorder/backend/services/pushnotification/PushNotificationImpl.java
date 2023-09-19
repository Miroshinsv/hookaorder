package ru.hookaorder.backend.services.pushnotification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PushNotificationImpl implements IPushNotificationService {
  private static final int ZERO_ELEMENTS = 0;
  private static final String EMPTY_STRING = "";
  private final FirebaseMessaging firebaseMessaging;

  @Autowired
  public PushNotificationImpl(@Value("${fcm.credentials.app.file.name}") String credentials) {
    this.firebaseMessaging = FirebaseMessaging.getInstance(getFirebaseApp(credentials));
  }

  @SneakyThrows
  private static FirebaseApp getFirebaseApp(String fmcCredentials) {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials
        .fromStream(Objects.requireNonNull(classloader.getResourceAsStream(fmcCredentials)))).build();
    return FirebaseApp.initializeApp(options);
  }

  private Notification buildUserNotification(String title, String body) {
    return Notification
        .builder()
        .setTitle(title)
        .setBody(body)
        .build();
  }

  private Message buildMessage(String FMCToken, Notification userPushNotification) {
    return Message
        .builder()
        .setToken(FMCToken)
        .setNotification(userPushNotification)
        .build();
  }

  @Override
  @SneakyThrows
  public String sendNotificationChangeOrderStatusUser(UserEntity user, OrderEntity order, EOrderStatus status) {
    if (user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
      return null;
    }
    switch (status) {
      case TAKEN -> {
        return firebaseMessaging.send(buildMessage(user.getFcmToken(), buildUserNotification("Ваш заказ принят", String.format("Ждем вас по адресу %s в %s", order.getPlaceId().getAddress().getAddress(), order.getOrderTime()))));
      }
      case CANCELLED -> {
        return firebaseMessaging.send(buildMessage(user.getFcmToken(), buildUserNotification("Ваш заказ отмен", String.format("К сожалению, ваш заказ  по адресу %s на %s отменен", order.getPlaceId().getAddress().getAddress(), order.getOrderTime()))));
      }
      default -> {
        return null;
      }
    }
  }

  @SneakyThrows
  @Override
  public BatchResponse sendNotificationNewOrderToStaff(OrderEntity order, Set<String> FCMTokens) {
    if (!isNotEmptyValidTokens(FCMTokens)) {
      return null;
    }
    return firebaseMessaging
        .sendAll(
            FCMTokens
                .stream()
                .map(val -> buildMessage(val,
                    buildUserNotification("Новый заказ #".concat(order.getId().toString()),
                        String.format("Номер телефона:\n %s\nВремя:\n%s\nКомментарий:\n%s",
                            order.getUserId().getPhone(),
                            order.getOrderTime(),
                            order.getComment() != null ? order.getComment().getText() : EMPTY_STRING))))
                .collect(Collectors.toList())
        );
  }

  private boolean isNotEmptyValidTokens(Set<String> fcmTokens) {
    return Objects.nonNull(fcmTokens)
        && fcmTokens.stream().filter(Objects::nonNull)
        .map(String::trim).filter(Predicate.not(String::isEmpty))
        .count() > ZERO_ELEMENTS;
  }

  @Override
  public BatchResponse sendSubscribeMessage(Set<String> FCMTokens, String title, String message) throws FirebaseMessagingException {
    return firebaseMessaging
        .sendAll(
            FCMTokens
                .stream()
                .map(val -> buildMessage(val,
                    buildUserNotification(title, message)))
                .collect(Collectors.toList()
                )
        );
  }

}
