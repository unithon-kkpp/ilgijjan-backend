package com.ilgijjan.domain.billing.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.billing.domain.PaymentHistory
import com.ilgijjan.domain.billing.infrastructure.PaymentHistoryRepository
import org.springframework.stereotype.Component

@Component
class PaymentHistoryReader(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    fun getByPurchaseToken(purchaseToken: String): PaymentHistory {
        return paymentHistoryRepository.findById(purchaseToken)
            .orElseThrow { CustomException(ErrorCode.PAYMENT_HISTORY_NOT_FOUND) }
    }
}
