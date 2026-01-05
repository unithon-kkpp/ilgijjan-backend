package com.ilgijjan.integration.billing.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Component
class OneStoreSignatureVerifier(
    @Value("\${onestore.public-key}") private val publicKeyStr: String,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val SIGNATURE_FIELD = "signature"
        private const val ALGORITHM_RSA = "RSA"
        private const val SIGNING_ALGORITHM = "SHA512withRSA"
    }

    fun validate(rawBody: String, signature: String) {
        if (!verify(rawBody, signature)) {
            log.error("[Webhook Critical] 원스토어 서명 검증 실패. Public Key 설정이 틀렸거나 Body 형식이 변경되었을 수 있습니다. 3일 내에 설정을 수정하지 않으면 데이터가 누락됩니다. Signature: {}, Body: {}", signature, rawBody)
            throw IllegalStateException("Invalid OneStore Signature")
        }
    }

    private fun verify(rawBody: String, signature: String): Boolean {
        val root = objectMapper.readTree(rawBody) as ObjectNode
        root.remove(SIGNATURE_FIELD)

        val keyFactory = KeyFactory.getInstance(ALGORITHM_RSA)
        val keySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr))
        val publicKey = keyFactory.generatePublic(keySpec)

        val sig = Signature.getInstance(SIGNING_ALGORITHM)
        sig.initVerify(publicKey)
        sig.update(root.toString().toByteArray(Charsets.UTF_8))

        return sig.verify(Base64.getDecoder().decode(signature))
    }
}
