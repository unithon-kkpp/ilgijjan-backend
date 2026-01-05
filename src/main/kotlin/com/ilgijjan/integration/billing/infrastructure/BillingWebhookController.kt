package com.ilgijjan.integration.billing.infrastructure

import com.ilgijjan.domain.billing.domain.StoreType
import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/billing/webhooks")
class BillingWebhookController (
    private val handlers: List<BillingWebhookHandler>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/{storeType}")
    fun handleWebhook(
        @PathVariable storeType: String,
        @RequestBody rawBody: String
    ): ResponseEntity<Unit> {
        val type = StoreType.fromPath(storeType) ?: return ResponseEntity.badRequest().build()
        log.info("Billing Webhook 수신 - Store: {}, Body: {}", storeType, rawBody)
        val handler = handlers.find { it.getStoreType() == type } ?: return ResponseEntity.badRequest().build()

        handler.handle(rawBody)
        return ResponseEntity.ok().build()
    }
}
