package com.ilgijjan.domain.billing.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.billing.application.BillingService
import com.ilgijjan.domain.billing.domain.StoreType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing", description = "결제 관련 API입니다.")
class BillingController(
    private val billingService: BillingService
) {

    @PostMapping("/verify")
    @Operation(
        summary = "인앱 결제 영수증 검증 및 아이템 지급",
        description = """
            스토어 SDK를 통해 결제가 완료된 직후 호출합니다.
            전달받은 `purchaseToken`을 기반으로 영수증을 검증하며,
            검증에 성공하면 유료 아이템을 즉시 지급합니다.
        """
    )
    fun verify(
        @LoginUser userId: Long,
        @RequestBody @Valid request: BillingVerifyRequest
    ): ResponseEntity<Unit> {
        billingService.verifyPurchase(userId, request)
        return ResponseEntity.ok().build()
    }


    @GetMapping("/products")
    @Operation(summary = "스토어별 상품 목록 조회")
    fun getProducts(
        @RequestParam storeType: StoreType
    ): ResponseEntity<ReadProductsResponse> {
        return ResponseEntity.ok(billingService.getProducts(storeType))
    }
}
