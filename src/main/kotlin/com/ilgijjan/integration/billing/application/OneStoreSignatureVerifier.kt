package com.ilgijjan.integration.billing.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
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
    companion object {
        private const val SIGNATURE_FIELD = "signature"
        private const val ALGORITHM_RSA = "RSA"
        private const val SIGNING_ALGORITHM = "SHA512withRSA"
    }

    fun verify(rawBody: String, signature: String): Boolean {
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
