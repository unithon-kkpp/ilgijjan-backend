package com.ilgijjan.domain.diary.infrastructure

import com.ilgijjan.domain.diary.domain.Diary
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Diary d where d.id = :id")
    fun findByIdWithLock(id: Long): Diary?

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.isPublic = true ORDER BY d.id DESC")
    fun findPublicDiariesFirstPage(pageable: Pageable): Slice<Diary>

    @Query("""
        SELECT d FROM Diary d 
        JOIN FETCH d.user 
        WHERE d.isPublic = true 
          AND d.id < :lastId 
        ORDER BY d.id DESC
    """)
    fun findPublicDiariesNextPage(@Param("lastId") lastId: Long, pageable: Pageable): Slice<Diary>
}
