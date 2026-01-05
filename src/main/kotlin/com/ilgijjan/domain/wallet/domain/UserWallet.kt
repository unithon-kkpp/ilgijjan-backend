package com.ilgijjan.domain.wallet.domain

import com.ilgijjan.common.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class UserWallet(
    @Id
    val userId: Long,

    var noteCount: Int = 0,
) : BaseEntity() {

    fun charge(amount: Int) {
        require(amount > 0) { "충전 수량은 0보다 커야 합니다." }
        this.noteCount += amount
    }

    fun subtract(amount: Int) {
        require(amount > 0) { "차감 수량은 0보다 커야 합니다." }
        require(this.noteCount >= amount) {
            "보유 개수가 부족합니다. (userId: $userId, 보유: $noteCount, 요청: $amount)"
        }
        this.noteCount -= amount
    }
}
