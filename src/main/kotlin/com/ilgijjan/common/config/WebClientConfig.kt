package com.ilgijjan.common.config

import io.netty.channel.ChannelOption
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig {

    @Bean
    fun webClientBuilder(): WebClient.Builder {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
            .responseTimeout(Duration.ofSeconds(60))

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
    }
}