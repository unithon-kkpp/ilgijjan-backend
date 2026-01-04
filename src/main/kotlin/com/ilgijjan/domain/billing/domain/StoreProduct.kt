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
class StoreProduct(
    @Id
    val storeProductId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @Enumerated(EnumType.STRING)
    val storeType: StoreType
) : BaseEntity()
