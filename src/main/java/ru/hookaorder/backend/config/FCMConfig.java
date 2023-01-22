package ru.hookaorder.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FCMConfig {

    @Value("${fcm.credentials.file.name}")
    private String fcmCredsFile;

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(fcmCredsFile);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        System.out.println(">>>>");
        System.out.println(serviceAccount);
        System.out.println(">>>>");
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
