package com.ilgijjan.domain.diary.infrastructure

import com.ilgijjan.domain.diary.domain.Diary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {

    @Query("""
        SELECT d FROM Diary d 
        WHERE FUNCTION('YEAR', d.createdAt) = :year 
          AND FUNCTION('MONTH', d.createdAt) = :month
        ORDER BY d.createdAt DESC
    """)
    fun findAllByYearAndMonth(
        @Param("year") year: Int,
        @Param("month") month: Int
    ): List<Diary>
}