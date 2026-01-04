package com.ilgijjan.domain.billing.infrastructure

import com.ilgijjan.domain.billing.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>