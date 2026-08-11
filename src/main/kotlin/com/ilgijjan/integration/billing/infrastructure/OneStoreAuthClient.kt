package com.ilgijjan.integration.billing.infrastructure

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class OneStoreAuthClient(
    @Value("\${onestore.base-url}") private val baseUrl: String,
    @Value("\${onestore.client-id}") private val clientId: String,
    @Value("\${onestore.client-secret}") private val clientSecret: String,
    restClientBuilder: RestClient.Builder
) {
    @Volatile private var cachedToken: OneStoreAccessToken? = null
    private val restClient = restClientBuilder.baseUrl(baseUrl).build()

    @Synchronized
    fun getAccessToken(): String {
        if (cachedToken == null || cachedToken!!.isExpired()) {
            cachedToken = fetchNewToken()
        }
        return cachedToken!!.accessToken
    }

    private fun fetchNewToken(): OneStoreAccessToken {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("grant_type", "client_credentials")
        }

        val response = restClient.post()
            .uri("/v7/oauth/token")
            .header("x-market-code", "MKT_GLB")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(OneStoreTokenResponse::class.java) ?: throw CustomException(ErrorCode.ONE_STORE_AUTH_FAILED)

        return OneStoreAccessToken(
            accessToken = response.accessToken,
            expiresInSeconds = response.expiresIn
        )
    }
}
