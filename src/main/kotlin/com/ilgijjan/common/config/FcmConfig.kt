package com.ilgijjan.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FcmConfig(
    @Value("\${firebase.service-key.path}")
    private val serviceKeyPath: String
) {
    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val resource = ClassPathResource(serviceKeyPath)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
            .build()
        val app = if (FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(options) else FirebaseApp.getInstance()
        return FirebaseMessaging.getInstance(app)
    }
}
