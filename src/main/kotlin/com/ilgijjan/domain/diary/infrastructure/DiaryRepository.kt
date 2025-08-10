package com.ilgijjan.domain.diary.infrastructure

import com.ilgijjan.domain.diary.domain.Diary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {
}