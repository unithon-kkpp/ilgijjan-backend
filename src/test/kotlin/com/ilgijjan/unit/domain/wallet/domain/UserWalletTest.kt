package com.ilgijjan.unit.domain.wallet.domain

import com.ilgijjan.domain.wallet.domain.UserWallet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserWalletTest {

    @Test
    fun `음표 충전 성공`() {
        // given
        val wallet = UserWallet(userId = 1L, noteCount = 10)

        // when
        wallet.charge(5)

        // then
        assertThat(wallet.noteCount).isEqualTo(15)
    }

    @Test
    fun `음표 차감 성공`() {
        // given
        val wallet = UserWallet(userId = 1L, noteCount = 10)

        // when
        wallet.subtract(7)

        // then
        assertThat(wallet.noteCount).isEqualTo(3)
    }

    @Test
    fun `보유 개수보다 많은 수량 차감 시 예외 발생`() {
        // given
        val wallet = UserWallet(userId = 1L, noteCount = 5)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            wallet.subtract(10)
        }
        assertThat(exception.message).contains("보유 개수가 부족합니다")
    }

    @Test
    fun `0이하의 수량 차감 시 예외 발생`() {
        val wallet = UserWallet(userId = 1L, noteCount = 10)

        assertThrows<IllegalArgumentException> {
            wallet.subtract(0)
        }
        assertThrows<IllegalArgumentException> {
            wallet.subtract(-1)
        }
    }
}
