package com.ilgijjan.domain.like.application

import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.like.domain.Like
import com.ilgijjan.domain.user.domain.User
import org.springframework.stereotype.Component

@Component
class LikeManager(
    private val likeCreator: LikeCreator,
    private val likeUpdater: LikeUpdater,
    private val likeRemover: LikeRemover
) {
    fun add(diary: Diary, user: User, existingLike: Like?) {
        if (existingLike?.isActive() == true) return

        if (existingLike != null) {
            likeUpdater.active(existingLike)
        } else {
            likeCreator.create(diary, user)
        }
        diary.increaseLikeCount()
    }

    fun remove(diary: Diary, like: Like) {
        if (!like.isActive()) return

        likeRemover.remove(like)
        diary.decreaseLikeCount()
    }
}
