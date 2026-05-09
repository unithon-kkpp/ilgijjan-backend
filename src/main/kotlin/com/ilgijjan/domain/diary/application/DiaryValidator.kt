package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.domain.DiaryStatus
import org.springframework.stereotype.Component

@Component
class DiaryValidator {

    fun validateAccess(diary: Diary, userId: Long) {
        if (!isAccessible(diary, userId)) {
            throw CustomException(ErrorCode.PRIVATE_DIARY_ACCESS_DENIED)
        }
    }

    fun validateCompleted(diary: Diary) {
        if (diary.status != DiaryStatus.COMPLETED) {
            throw CustomException(ErrorCode.DIARY_NOT_COMPLETED)
        }
    }

    private fun isAccessible(diary: Diary, userId: Long): Boolean {
        return diary.isPublic || diary.user.id == userId
    }
}
