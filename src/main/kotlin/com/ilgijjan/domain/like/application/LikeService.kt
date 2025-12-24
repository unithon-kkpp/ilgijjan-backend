package com.ilgijjan.domain.like.application

import com.ilgijjan.domain.diary.application.DiaryReader
import com.ilgijjan.domain.user.application.UserReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LikeService (
    private val diaryReader: DiaryReader,
    private val userReader: UserReader,
    private val likeReader: LikeReader,
    private val likeManager: LikeManager
) {
    @Transactional
    fun addLike(diaryId: Long, userId: Long) {
        val diary = diaryReader.getDiaryByIdWithLock(diaryId)
        val user = userReader.getUserById(userId)
        val existingLike = likeReader.findByDiaryAndUser(diary, user)
        likeManager.add(diary, user, existingLike)
    }

    @Transactional
    fun removeLike(diaryId: Long, userId: Long) {
        val diary = diaryReader.getDiaryByIdWithLock(diaryId)
        val user = userReader.getUserById(userId)
        val like = likeReader.getByDiaryAndUser(diary, user)
        likeManager.remove(diary, like)
    }
}
