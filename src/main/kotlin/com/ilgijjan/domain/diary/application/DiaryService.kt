package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.annotation.CheckDiaryOwner
import com.ilgijjan.common.config.RabbitMqConfig
import com.ilgijjan.common.constants.WalletConstants
import com.ilgijjan.domain.diary.presentation.CreateDiaryRequest
import com.ilgijjan.domain.diary.presentation.CreateDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadMyDiariesResponse
import com.ilgijjan.domain.diary.presentation.ReadPublicDiariesResponse
import com.ilgijjan.domain.user.application.UserReader
import com.ilgijjan.domain.wallet.application.UserWalletUpdater
import com.ilgijjan.integration.messaging.application.MessageProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DiaryService(
    private val diaryCreator: DiaryCreator,
    private val diaryReader: DiaryReader,
    private val diaryUpdater: DiaryUpdater,
    private val diaryValidator: DiaryValidator,
    private val messageProducer: MessageProducer,
    private val userReader: UserReader,
    private val userWalletUpdater: UserWalletUpdater
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

        messageProducer.sendMessage(
            exchange = RabbitMqConfig.DIARY_EXCHANGE,
            routingKey = RabbitMqConfig.DIARY_ROUTING_KEY,
            payload = diary.id!!
        )

        return CreateDiaryResponse(diary.id)
    }

    fun getDiaryById(diaryId: Long, userId: Long): ReadDiaryResponse {
        val diary = diaryReader.getDiaryById(diaryId)
        diaryValidator.validateAccess(diary, userId)
        val isOwner = diary.user.id == userId
        return ReadDiaryResponse.from(diary, isOwner)
    }

    fun getMyDiariesByYearAndMonth(userId: Long, year: Int, month: Int): ReadMyDiariesResponse {
        val diaries = diaryReader.findAllByUserIdAndDate(userId, year, month)
        return ReadMyDiariesResponse.from(diaries)
    }

    fun getPublicDiaries(lastId: Long?, size: Int): ReadPublicDiariesResponse {
        val diariesSlice = diaryReader.findAllPublicWithSlice(lastId, size)
        return ReadPublicDiariesResponse.from(diariesSlice)
    }

    @Transactional
    @CheckDiaryOwner
    fun publishDiary(diaryId: Long) {
        diaryUpdater.publish(diaryId)
    }

    @Transactional
    @CheckDiaryOwner
    fun unpublishDiary(diaryId: Long) {
        diaryUpdater.unpublish(diaryId)
    }
}
