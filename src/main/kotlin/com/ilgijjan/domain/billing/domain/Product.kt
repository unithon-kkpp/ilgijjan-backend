package com.ilgijjan.domain.billing.domain

import com.ilgijjan.common.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    val noteAmount: Int,

    val price: Long,

    val isActive: Boolean = true
) : BaseEntity()
