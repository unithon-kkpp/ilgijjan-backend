package com.tadadiary.diary.infrastructure

import com.tadadiary.diary.domain.Diary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {
}