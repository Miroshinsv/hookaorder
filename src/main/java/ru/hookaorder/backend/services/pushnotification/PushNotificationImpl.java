package ru.hookaorder.backend.services.pushnotification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hookaorder.backend.feature.order.entity.EOrderStatus;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PushNotificationImpl implements IPushNotificationService {
    private final FirebaseMessaging firebaseMessaging;

    @Autowired
    public PushNotificationImpl(@Value("${fcm.credentials.app.file.name}") String credentials) {
        this.firebaseMessaging = FirebaseMessaging.getInstance(getFirebaseApp(credentials));
    }

    @SneakyThrows
    private FirebaseApp getFirebaseApp(String fmcCredentials) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(Objects.requireNonNull(classloader.getResourceAsStream(fmcCredentials)))).build();
        return FirebaseApp.initializeApp(options);
    }

    @Override
    @SneakyThrows
    public String sendNotificationChangeOderStatusUser(UserEntity user, OrderEntity order, EOrderStatus status) {
        switch (status) {
            case TAKEN -> {
                return firebaseMessaging.send(Message.builder().setNotification(Notification.builder().setTitle("Ваш заказ принят!").setBody(String.format("Ждем вас в %s по адресу %s ", order.getOrderTime(), order.getPlaceId().getAddress())).build()).build());

            }
            case CANCELLED -> {
                return firebaseMessaging.send(Message.builder().setNotification(Notification.builder().setTitle("Ваш заказ отменет").setBody(String.format("Ваш заказ на %s по адресу %s был отменен", order.getOrderTime(), order.getPlaceId().getAddress())).build()).build());
            }
            default -> {
                return null;
            }
        }
    }

    @SneakyThrows
    @Override
    public BatchResponse sendNotificationNewOrderToStuff(OrderEntity order, List<String> FMCTokens) {
        return firebaseMessaging.sendAll(FMCTokens.stream().map(val -> Message.builder().setNotification(Notification.builder().setTitle("Новый заказ " + order.getId()).setBody(String.format("Номер телефона:\n %s\nВремя:\n%s\nКомментарий:\n%s", order.getUserId().getPhone(), order.getOrderTime(), order.getComment().getText())).build()).build()).collect(Collectors.toList()));
    }


}
