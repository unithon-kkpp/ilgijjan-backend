package com.tadadiary.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
import org.springframework.beans.factory.annotation.Value
import java.io.IOException


@Configuration
class StorageConfig {

    @Value("\${spring.cloud.gcp.storage.credentials.location}")
    private lateinit var keyFileLocation: String

    @Bean
    @Throws(IOException::class)
    fun storage(): Storage {
        val keyFile = ResourceUtils.getURL(keyFileLocation).openStream()
        return StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(keyFile))
            .build()
            .service
    }
}