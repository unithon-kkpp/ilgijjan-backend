package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.annotation.LogExecutionTime
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.domain.DiaryInputType
import com.ilgijjan.domain.fcmtoken.application.FcmTokenDeleter
import com.ilgijjan.domain.fcmtoken.application.FcmTokenReader
import com.ilgijjan.integration.image.application.ImageGenerator
import com.ilgijjan.integration.music.application.MusicGenerator
import com.ilgijjan.integration.notification.application.NotificationSender
import com.ilgijjan.integration.ocr.application.OcrProcessor
import com.ilgijjan.integration.text.application.TextRefiner
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class DiaryTaskProcessor(
    private val diaryReader: DiaryReader,
    private val diaryUpdater: DiaryUpdater,
    private val diaryFailureHandler: DiaryFailureHandler,
    private val ocrProcessor: OcrProcessor,
    private val textRefiner: TextRefiner,
    private val imageGenerator: ImageGenerator,
    private val musicGenerator: MusicGenerator,
    private val fcmTokenReader: FcmTokenReader,
    private val fcmTokenDeleter: FcmTokenDeleter,
    private val notificationSender: NotificationSender
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Async("diaryTaskExecutor")
    @LogExecutionTime
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun process(diaryId: Long) {
        log.info("비동기 일기 생성 시작 - ID: $diaryId")
        val diary = diaryReader.getDiaryById(diaryId)

        try {
            val baseText = diary.extractedText ?: when (diary.type) {
                DiaryInputType.PHOTO -> {
                    log.info("PHOTO 타입: OCR 추출 시작")
                    val photoUrl = requireNotNull(diary.photoUrl) { "PHOTO 타입 일기에 photoUrl이 누락되었습니다. ID: $diaryId" }
                    val extracted = ocrProcessor.extractText(photoUrl)
                    diaryUpdater.saveExtractedText(diaryId, extracted)
                    extracted
                }
                DiaryInputType.TEXT -> {
                    log.info("TEXT 타입: 입력된 텍스트 사용")
                    requireNotNull(diary.text) { "TEXT 타입 일기에 text가 누락되었습니다. ID: $diaryId" }
                }
            }

            val refinedText = diary.refinedText ?: textRefiner.refineText(baseText).also {
                diaryUpdater.saveRefinedText(diaryId, it)
            }

            val musicFuture = if (diary.musicUrl == null) musicGenerator.generateMusicAsync(refinedText) else null

            if (diary.imageUrl == null) {
                val imageUrl = imageGenerator.generateImage(refinedText, diary.weather)
                diaryUpdater.saveImage(diaryId, imageUrl)
            }

            if (musicFuture != null) {
                val musicResult = musicFuture.get()
                diaryUpdater.saveMusic(diaryId, musicResult.audioUrl, musicResult.lyrics)
            }

            diaryUpdater.complete(diaryId)

            log.info("비동기 일기 생성 완료 - ID: $diaryId")
        } catch (e: Exception) {
            log.error("일기 생성 중 에러 발생 - ID: $diaryId, 사유: ${e.message}")
            diaryFailureHandler.handle(diaryId, diary.user.id!!)
            sendNotification(diary, false)
            return
        }

        sendNotification(diary, true)
    }

    private fun sendNotification(diary: Diary, isSuccess: Boolean) {
        val tokens = fcmTokenReader.findAllByUserId(diary.user.id!!).map { it.token }
        val deadTokens = if (isSuccess) {
            notificationSender.sendDiaryCompletion(tokens, diary.id!!)
        } else {
            notificationSender.sendDiaryFailure(tokens, diary.id!!)
        }
        fcmTokenDeleter.deleteByTokens(deadTokens)
    }
}
