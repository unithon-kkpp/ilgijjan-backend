package com.ilgijjan.domain.fcmtoken.infrastructure

import com.ilgijjan.domain.fcmtoken.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {
    fun findAllByUserId(userId: Long): List<FcmToken>
    fun existsByToken(token: String): Boolean
    fun findByToken(token: String): FcmToken?

    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.token IN :tokens")
    fun deleteByTokenIn(tokens: List<String>)

    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.lastUsedAt < :dateTime")
    fun deleteByLastUsedAtBefore(dateTime: LocalDateTime)
}
