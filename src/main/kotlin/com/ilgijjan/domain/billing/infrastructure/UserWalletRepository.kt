package com.ilgijjan.domain.billing.infrastructure

import com.ilgijjan.domain.billing.domain.UserWallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserWalletRepository : JpaRepository<UserWallet, Long>