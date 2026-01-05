package com.ilgijjan.domain.wallet.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserWalletUpdater(
    private val userWalletReader: UserWalletReader,
    private val userWalletValidator: UserWalletValidator
) {
    @Transactional
    fun charge(userId: Long, amount: Int) {
        val wallet = userWalletReader.getByUserIdForUpdate(userId)
        wallet.charge(amount)
    }

    @Transactional
    fun revoke(userId: Long, amount: Int) {
        val wallet = userWalletReader.getByUserIdForUpdate(userId)
        wallet.subtract(amount)
    }

    @Transactional
    fun consume(userId: Long, amount: Int) {
        val wallet = userWalletReader.getByUserIdForUpdate(userId)
        userWalletValidator.validateHasEnoughNotes(wallet, amount)
        wallet.subtract(amount)
    }
}
