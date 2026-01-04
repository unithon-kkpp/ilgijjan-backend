package com.ilgijjan.domain.billing.domain

import com.ilgijjan.common.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class UserWallet(
    @Id
    val userId: Long,

    var noteBalance: Int = 0,
) : BaseEntity() {

    fun charge(amount: Int) {
        require(amount > 0) { "충전 수량은 0보다 커야 합니다." }
        this.noteBalance += amount
    }
}
