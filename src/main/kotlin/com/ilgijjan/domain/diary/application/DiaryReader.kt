package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.infrastructure.DiaryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DiaryReader(
    private val diaryRepository: DiaryRepository
) {
    fun getDiaryById(diaryId: Long): Diary {
        return diaryRepository.findById(diaryId)
            .orElseThrow { CustomException(ErrorCode.DIARY_NOT_FOUND) }
    }

    fun getDiaryByIdWithLock(diaryId: Long): Diary {
        return diaryRepository.findByIdWithLock(diaryId)
            ?: throw CustomException(ErrorCode.DIARY_NOT_FOUND)
    }

    fun findAllByUserIdAndDate(userId: Long, year: Int, month: Int): List<Diary> {
        val startOfMonth = LocalDateTime.of(year, month, 1, 0, 0)
        val endOfMonth = startOfMonth.plusMonths(1)
        return diaryRepository.findAllByUserIdAndDateRange(userId, startOfMonth, endOfMonth)
    }

    fun findAllPublicWithSlice(lastId: Long?, size: Int): Slice<Diary> {
        val pageRequest = PageRequest.of(0, size)

        return if (lastId == null) {
            diaryRepository.findByIsPublicTrueOrderByIdDesc(pageRequest)
        } else {
            diaryRepository.findByIsPublicTrueAndIdLessThanOrderByIdDesc(lastId, pageRequest)
        }
    }
}
