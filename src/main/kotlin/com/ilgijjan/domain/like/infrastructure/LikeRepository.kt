package com.ilgijjan.domain.like.infrastructure

import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.like.domain.Like
import com.ilgijjan.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : JpaRepository<Like, Long> {
    fun findByDiaryAndUser(diary: Diary, user: User): Like?
}
