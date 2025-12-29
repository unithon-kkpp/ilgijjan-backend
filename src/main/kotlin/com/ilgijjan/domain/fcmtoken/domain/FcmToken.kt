package com.ilgijjan.domain.fcmtoken.domain

import com.ilgijjan.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class FcmToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false, unique = true)
    val token: String,

    var lastUsedAt: LocalDateTime = LocalDateTime.now()
): BaseEntity() {
    fun updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now()
    }

    fun updateUserId(userId: Long) {
        this.userId = userId
    }
}
