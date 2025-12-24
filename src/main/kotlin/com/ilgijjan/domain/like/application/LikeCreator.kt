package com.ilgijjan.domain.like.application

import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.like.domain.Like
import com.ilgijjan.domain.like.infrastructure.LikeRepository
import com.ilgijjan.domain.user.domain.User
import org.springframework.stereotype.Component

@Component
class LikeCreator(
    private val likeRepository: LikeRepository
) {
    fun create(diary: Diary, user: User) {
        likeRepository.save(Like(user = user, diary = diary))
    }
}
