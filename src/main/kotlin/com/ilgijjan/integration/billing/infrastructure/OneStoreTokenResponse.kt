package com.ilgijjan.integration.billing.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class OneStoreTokenResponse(
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expires_in") val expiresIn: Long,
    @JsonProperty("scope") val scope: String
)
