package com.ilgijjan.unit.domain.wallet.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.wallet.application.UserWalletValidator
import com.ilgijjan.domain.wallet.domain.UserWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserWalletValidatorTest {

    private val validator = UserWalletValidator()

    @Test
    fun `보유 개수가 충분하면 예외가 발생하지 않는다`() {
        // given
        val wallet = UserWallet(userId = 1L, noteCount = 10)

        // when & then
        assertDoesNotThrow {
            validator.validateHasEnoughNotes(wallet, 10)
        }
    }

    @Test
    fun `보유 개수가 부족하면 InsufficientNoteException이 발생한다`() {
        // given
        val wallet = UserWallet(userId = 1L, noteCount = 5)

        // when & then
        val exception = assertThrows<CustomException> {
            validator.validateHasEnoughNotes(wallet, 10)
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INSUFFICIENT_NOTES)
    }
}
