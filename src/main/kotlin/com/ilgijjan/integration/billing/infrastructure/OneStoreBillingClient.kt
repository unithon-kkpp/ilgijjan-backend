package com.ilgijjan.integration.billing.infrastructure

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import kotlin.jvm.java

@Component
class OneStoreBillingClient(
    private val authClient: OneStoreAuthClient,
    @Value("\${onestore.base-url}") private val baseUrl: String,
    @Value("\${onestore.client-id}") private val clientId: String,
    restClientBuilder: RestClient.Builder
) {
    private val restClient = restClientBuilder.baseUrl(baseUrl).build()

    fun getPurchaseDetails(productId: String, purchaseToken: String): OneStoreGetPurchaseDetailsResponse {
        return restClient.get()
            .uri("/v7/apps/$clientId/purchases/inapp/products/$productId/$purchaseToken")
            .header("Authorization", "Bearer ${authClient.getAccessToken()}")
            .header("x-market-code", "MKT_GLB")
            .retrieve()
            .body(OneStoreGetPurchaseDetailsResponse::class.java) ?: throw CustomException(ErrorCode.ONE_STORE_VERIFY_FAILED)
    }

    fun consumePurchase(productId: String, purchaseToken: String) {
        restClient.post()
            .uri("/v7/apps/$clientId/purchases/inapp/products/$productId/$purchaseToken/consume")
            .header("Authorization", "Bearer ${authClient.getAccessToken()}")
            .header("x-market-code", "MKT_GLB")
            .body(mapOf("developerPayload" to ""))
            .retrieve()
            .body(Map::class.java) ?: throw CustomException(ErrorCode.ONE_STORE_CONSUME_FAILED)
    }
}
