package com.ilgijjan.domain.billing.application

import com.ilgijjan.domain.billing.domain.UserWallet
import com.ilgijjan.domain.billing.infrastructure.UserWalletRepository
import org.springframework.stereotype.Component

@Component
class UserWalletManager(
    private val walletRepository: UserWalletRepository
) {
    fun charge(userId: Long, amount: Int) {
        val wallet = walletRepository.findById(userId)
            .orElseGet { walletRepository.save(UserWallet(userId = userId)) }
        wallet.charge(amount)
    }
}
