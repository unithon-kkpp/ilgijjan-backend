package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.annotation.LogExecutionTime
import com.ilgijjan.common.constants.WalletConstants
import com.ilgijjan.domain.diary.domain.DiaryInputType
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.Semaphore
import com.ilgijjan.domain.fcmtoken.application.FcmTokenDeleter
import com.ilgijjan.domain.fcmtoken.application.FcmTokenReader
import com.ilgijjan.domain.wallet.application.UserWalletUpdater
import com.ilgijjan.integration.image.application.ImageGenerator
import com.ilgijjan.integration.music.application.MusicGenerator
import com.ilgijjan.integration.notification.application.NotificationSender
import com.ilgijjan.integration.ocr.application.OcrProcessor
import com.ilgijjan.integration.text.application.TextRefiner
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class DiaryTaskProcessor(
    private val diaryReader: DiaryReader,
    private val diaryUpdater: DiaryUpdater,
    private val ocrProcessor: OcrProcessor,
    private val textRefiner: TextRefiner,
    private val imageGenerator: ImageGenerator,
    private val musicGenerator: MusicGenerator,
    private val fcmTokenReader: FcmTokenReader,
    private val fcmTokenDeleter: FcmTokenDeleter,
    private val notificationSender: NotificationSender,
    private val userWalletUpdater: UserWalletUpdater
) {
    private val log = LoggerFactory.getLogger(javaClass)
    // asyncExecutor 기본 쓰레드 수(corePoolSize=24) / 일기 1건당 쓰레드 수(process 1 + music 1 + image 1 = 3) = 8
    private val semaphore = Semaphore(8)

    @Async("asyncExecutor")
    @LogExecutionTime
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun process(diaryId: Long) {
        semaphore.acquire()
        try {
            log.info("비동기 일기 생성 시작 - ID: $diaryId")
            val diary = diaryReader.getDiaryById(diaryId)

            try {
                val baseText = when (diary.type) {
                    DiaryInputType.PHOTO -> {
                        log.info("PHOTO 타입: OCR 추출 시작")
                        ocrProcessor.extractText(diary.photoUrl!!)
                    }
                    DiaryInputType.TEXT -> {
                        log.info("TEXT 타입: 입력된 텍스트 사용")
                        diary.text
                    }
                }

                val refinedText = textRefiner.refineText(baseText!!)

                val musicFuture = musicGenerator.generateMusicAsync(refinedText)
                val imageFuture = imageGenerator.generateImageAsync(refinedText, diary.weather)

                val musicResult = musicFuture.get()
                val imageUrl = imageFuture.get()

                val updateCommand = UpdateDiaryResultCommand.of(imageUrl, musicResult)
                diaryUpdater.updateResult(diaryId, updateCommand)

                if (diary.user.isNotificationEnabled) {
                    val tokens = fcmTokenReader.findAllByUserId(diary.user.id!!).map { it.token }
                    val deadTokens = notificationSender.sendDiaryCompletion(tokens, diaryId)
                    fcmTokenDeleter.deleteByTokens(deadTokens)
                }

                log.info("비동기 일기 생성 완료 - ID: $diaryId")
            } catch (e: Exception) {
                log.error("일기 생성 중 에러 발생 - ID: $diaryId, 사유: ${e.message}")
                diaryUpdater.fail(diaryId)
                val userId = diary.user.id!!
                userWalletUpdater.charge(userId, WalletConstants.DIARY_CREATION_COST)

                if (diary.user.isNotificationEnabled) {
                    val tokens = fcmTokenReader.findAllByUserId(userId).map { it.token }
                    val deadTokens = notificationSender.sendDiaryFailure(tokens, diaryId)
                    fcmTokenDeleter.deleteByTokens(deadTokens)
                }
            }
        } finally {
            semaphore.release()
        }
    }
}
