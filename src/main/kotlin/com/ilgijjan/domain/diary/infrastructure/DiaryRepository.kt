package com.ilgijjan.domain.diary.infrastructure

import com.ilgijjan.domain.diary.domain.Diary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {

    @Query("""
        SELECT d FROM Diary d 
        WHERE d.user.id = :userId
          AND d.createdAt >= :startOfMonth 
          AND d.createdAt < :endOfMonth
        ORDER BY d.createdAt DESC
    """)
    fun findAllByUserIdAndDateRange(
        @Param("userId") userId: Long,
        @Param("startOfMonth") startOfMonth: LocalDateTime,
        @Param("endOfMonth") endOfMonth: LocalDateTime
    ): List<Diary>
}
