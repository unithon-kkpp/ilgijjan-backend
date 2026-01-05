package com.ilgijjan.domain.wallet.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserWalletUpdater(
    private val userWalletReader: UserWalletReader
) {
    @Transactional
    fun charge(userId: Long, amount: Int) {
        val wallet = userWalletReader.getByUserIdForUpdate(userId)
        wallet.charge(amount)
    }

    @Transactional
    fun revoke(userId: Long, amount: Int) {
        val wallet = userWalletReader.getByUserIdForUpdate(userId)
        wallet.revoke(amount)
    }
}
