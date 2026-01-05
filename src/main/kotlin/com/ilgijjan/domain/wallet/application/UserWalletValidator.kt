package com.ilgijjan.domain.wallet.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.wallet.domain.UserWallet
import org.springframework.stereotype.Component

@Component
class UserWalletValidator {

    fun validateHasEnoughNotes(wallet: UserWallet, requiredAmount: Int) {
        if (wallet.noteCount < requiredAmount) {
            throw CustomException(ErrorCode.INSUFFICIENT_NOTES)
        }
    }
}
