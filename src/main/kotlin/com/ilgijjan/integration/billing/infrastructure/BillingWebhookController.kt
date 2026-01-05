package com.ilgijjan.integration.billing.infrastructure

import com.ilgijjan.integration.billing.application.BillingWebhookService
import io.swagger.v3.oas.annotations.Hidden
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
    private val billingWebhookService: BillingWebhookService
) {
    @PostMapping("/{storeType}")
    fun handleWebhook(
        @PathVariable storeType: String,
        @RequestBody rawBody: String
    ): ResponseEntity<Unit> {
        billingWebhookService.processWebhook(storeType, rawBody)
        return ResponseEntity.ok().build()
    }
}
