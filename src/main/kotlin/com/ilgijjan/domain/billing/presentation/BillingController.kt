package com.ilgijjan.domain.billing.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.billing.application.BillingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing", description = "결제 관련 API입니다.")
class BillingController(
    private val billingService: BillingService
) {

    @PostMapping("/verify")
    @Operation(summary = "인앱 결제 영수증 검증")
    fun verify(
        @LoginUser userId: Long,
        @RequestBody @Valid request: BillingVerifyRequest
    ): ResponseEntity<Unit> {
        billingService.verifyPurchase(userId, request)
        return ResponseEntity.ok().build()
    }
}
