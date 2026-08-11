package com.ilgijjan.common.config

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig {

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        val requestFactory = ClientHttpRequestFactoryBuilder.detect()
            .build(
                ClientHttpRequestFactorySettings.defaults()
                    .withConnectTimeout(Duration.ofSeconds(10))
                    .withReadTimeout(Duration.ofSeconds(60))
            )

        return RestClient.builder()
            .requestFactory(requestFactory)
    }
}
