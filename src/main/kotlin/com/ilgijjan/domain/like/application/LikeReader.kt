package com.ilgijjan.domain.like.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.like.domain.Like
import com.ilgijjan.domain.like.domain.LikeStatus
import com.ilgijjan.domain.like.infrastructure.LikeRepository
import com.ilgijjan.domain.user.domain.User
import org.springframework.stereotype.Component

@Component
class LikeReader(
    private val likeRepository: LikeRepository
) {
    fun getByDiaryAndUser(diary: Diary, user: User): Like {
        return findByDiaryAndUser(diary, user)
            ?: throw CustomException(ErrorCode.LIKE_NOT_FOUND)
    }

    fun findByDiaryAndUser(diary: Diary, user: User): Like? {
        return likeRepository.findByDiaryAndUser(diary, user)
    }

    fun isLiked(diaryId: Long, userId: Long): Boolean {
        return likeRepository.existsByDiaryIdAndUserIdAndStatus(diaryId, userId, LikeStatus.ACTIVE)
    }
}
