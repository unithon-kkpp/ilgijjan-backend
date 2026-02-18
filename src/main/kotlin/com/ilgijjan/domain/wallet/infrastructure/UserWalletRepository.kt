package com.ilgijjan.domain.wallet.infrastructure

import com.ilgijjan.domain.wallet.domain.UserWallet
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserWalletRepository : JpaRepository<UserWallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from UserWallet w where w.userId = :userId")
    fun findByUserIdWithLock(userId: Long): Optional<UserWallet>

    fun findByUserId(userId: Long): Optional<UserWallet>

    @Modifying
    @Query("update UserWallet w set w.noteCount = 10")
    fun resetAllNoteCount()
}
