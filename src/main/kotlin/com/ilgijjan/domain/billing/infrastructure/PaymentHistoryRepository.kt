package com.ilgijjan.domain.billing.infrastructure

import com.ilgijjan.domain.billing.domain.PaymentHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentHistoryRepository : JpaRepository<PaymentHistory, String>