package com.ilgijjan.domain.billing.domain

import com.ilgijjan.common.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class PaymentHistory(
    @Id
    val purchaseToken: String,

    val userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_product_id")
    val storeProduct: StoreProduct,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus
) : BaseEntity() {

    fun complete() {
        this.status = PaymentStatus.COMPLETED
    }

    fun consumeFail() {
        this.status = PaymentStatus.CONSUME_FAILED
    }
}
