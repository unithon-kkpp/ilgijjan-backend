package com.ilgijjan.domain.billing.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.billing.domain.UserWallet
import com.ilgijjan.domain.billing.infrastructure.UserWalletRepository
import org.springframework.stereotype.Component

@Component
class UserWalletReader(
    private val walletRepository: UserWalletRepository
) {
    fun getByUserIdForUpdate(userId: Long): UserWallet {
        return walletRepository.findByUserIdWithLock(userId)
            .orElseThrow { CustomException(ErrorCode.WALLET_NOT_FOUND) }
    }
}
