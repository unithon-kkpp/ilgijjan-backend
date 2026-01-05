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

    fun revoke(amount: Int) {
        require(amount > 0) { "회수 금액은 0보다 커야 합니다." }
        require(this.noteCount >= amount) {
            "잔액이 부족하여 환불(회수) 처리를 할 수 없습니다. (userId: $userId, balance: $noteCount, requested: $amount)"
        }
        this.noteCount -= amount
    }
}
