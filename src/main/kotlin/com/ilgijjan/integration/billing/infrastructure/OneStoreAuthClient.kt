package com.ilgijjan.integration.billing.infrastructure

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class OneStoreAuthClient(
    @Value("\${onestore.base-url}") private val baseUrl: String,
    @Value("\${onestore.client-id}") private val clientId: String,
    @Value("\${onestore.client-secret}") private val clientSecret: String,
    webClientBuilder: WebClient.Builder
) {
    private var cachedToken: String? = null
    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    fun getAccessToken(): String {
        // TODO: 유효시간 체크 로직 추가 (보통 1시간)
        return cachedToken ?: fetchNewToken()
    }

    private fun fetchNewToken(): String {
        val response = webClient.post()
            .uri("/v7/oauth/token")
            .header("x-market-code", "MKT_GLB")
            .body(BodyInserters.fromFormData("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("grant_type", "client_credentials"))
            .retrieve()
            .bodyToMono(OneStoreTokenResponse::class.java)
            .block() ?: throw CustomException(ErrorCode.ONE_STORE_AUTH_FAILED)

        return response.accessToken.also { cachedToken = it }
    }
}
