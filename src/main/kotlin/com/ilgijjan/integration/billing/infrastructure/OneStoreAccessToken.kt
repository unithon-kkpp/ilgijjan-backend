package com.ilgijjan.integration.billing.infrastructure

data class OneStoreAccessToken(
    val accessToken: String,
    val expiresInSeconds: Long,
    val issuedAtMillis: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean {
        val bufferMillis = 600 * 1000L
        val expiryMillis = expiresInSeconds * 1000L
        return System.currentTimeMillis() - issuedAtMillis >= (expiryMillis - bufferMillis)
    }
}
