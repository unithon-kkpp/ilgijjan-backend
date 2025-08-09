package com.ilgijjan.diary.infrastructure

import com.ilgijjan.diary.domain.Diary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {
}