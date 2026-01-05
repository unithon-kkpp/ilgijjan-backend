package com.ilgijjan.domain.billing.infrastructure

import com.ilgijjan.domain.billing.domain.PaymentHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentHistoryRepository : JpaRepository<PaymentHistory, String> {

    @Query("""
        select ph from PaymentHistory ph 
        join fetch ph.storeProduct sp 
        join fetch sp.product 
        where ph.purchaseToken = :purchaseToken
    """)
    fun findByPurchaseTokenWithProduct(purchaseToken: String): PaymentHistory?
}
