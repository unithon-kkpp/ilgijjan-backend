package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.annotation.CheckDiaryOwner
import com.ilgijjan.common.config.CacheConfig
import com.ilgijjan.common.constants.WalletConstants
import com.ilgijjan.domain.diary.presentation.CreateDiaryRequest
import com.ilgijjan.domain.diary.presentation.CreateDiaryResponse
import com.ilgijjan.domain.diary.presentation.DiaryStatusResponse
import com.ilgijjan.domain.diary.presentation.ReadDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadMyDiariesResponse
import com.ilgijjan.domain.diary.presentation.ReadPublicDiariesResponse
import com.ilgijjan.domain.like.application.LikeReader
import com.ilgijjan.domain.user.application.UserReader
import com.ilgijjan.domain.wallet.application.UserWalletUpdater
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DiaryService(
    private val diaryCreator: DiaryCreator,
    private val diaryReader: DiaryReader,
    private val diaryDeleter: DiaryDeleter,
    private val diaryUpdater: DiaryUpdater,
    private val diaryValidator: DiaryValidator,
    private val likeReader: LikeReader,
    private val diaryTaskProcessor: DiaryTaskProcessor,
    private val userReader: UserReader,
    private val userWalletUpdater: UserWalletUpdater,
    private val eventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun createDiaryWithDummy(userId: Long, request: CreateDiaryRequest): CreateDiaryResponse {
        val user = userReader.getUserById(userId)
        val command = CreateDiaryCommand.of(request, user)
        val diary = diaryCreator.create(command)
        return CreateDiaryResponse(diary.id!!)
    }

    @Transactional
    fun createDiary(userId: Long, request: CreateDiaryRequest): CreateDiaryResponse {
        userWalletUpdater.subtract(userId, WalletConstants.DIARY_CREATION_COST)

        val user = userReader.getUserById(userId)
        val command = CreateDiaryCommand.of(request, user)
        val diary = diaryCreator.create(command)

        eventPublisher.publishEvent(DiaryCreatedEvent(diary.id!!))

        return CreateDiaryResponse(diary.id)
    }

    fun getDiaryById(diaryId: Long, userId: Long): ReadDiaryResponse {
        val diary = diaryReader.getDiaryById(diaryId)
        diaryValidator.validateAccess(diary, userId)
        diaryValidator.validateCompleted(diary)
        val isOwner = diary.user.id == userId
        val isLiked = likeReader.isLiked(diaryId, userId)
        return ReadDiaryResponse.from(diary, isOwner, isLiked)
    }

    @CheckDiaryOwner
    fun getDiaryStatus(diaryId: Long): DiaryStatusResponse {
        val diary = diaryReader.getDiaryById(diaryId)
        return DiaryStatusResponse.from(diary)
    }

    fun getMyDiariesByYearAndMonth(userId: Long, year: Int, month: Int): ReadMyDiariesResponse {
        val diaries = diaryReader.findAllByUserIdAndDate(userId, year, month)
        return ReadMyDiariesResponse.from(diaries)
    }

    @Cacheable(
        cacheNames = [CacheConfig.PUBLIC_DIARIES_CACHE],
        key = "(#lastId ?: 0) + ':' + #size"
    )
    fun getPublicDiaries(lastId: Long?, size: Int): ReadPublicDiariesResponse {
        val diariesSlice = diaryReader.findAllPublicWithSlice(lastId, size)
        return ReadPublicDiariesResponse.from(diariesSlice)
    }

    @Transactional
    @CheckDiaryOwner
    @CacheEvict(cacheNames = [CacheConfig.PUBLIC_DIARIES_CACHE], allEntries = true)
    fun publishDiary(diaryId: Long) {
        diaryUpdater.publish(diaryId)
    }

    @Transactional
    @CheckDiaryOwner
    @CacheEvict(cacheNames = [CacheConfig.PUBLIC_DIARIES_CACHE], allEntries = true)
    fun unpublishDiary(diaryId: Long) {
        diaryUpdater.unpublish(diaryId)
    }

    @Transactional
    @CheckDiaryOwner
    @CacheEvict(cacheNames = [CacheConfig.PUBLIC_DIARIES_CACHE], allEntries = true)
    fun deleteDiary(diaryId: Long) {
        diaryDeleter.delete(diaryId)
    }
}
