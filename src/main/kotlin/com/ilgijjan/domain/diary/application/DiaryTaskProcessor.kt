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
            val baseText = when (diary.type) {
                DiaryInputType.PHOTO -> {
                    log.info("PHOTO 타입: OCR 추출 시작")
                    val photoUrl = requireNotNull(diary.photoUrl) { "PHOTO 타입 일기에 photoUrl이 누락되었습니다. ID: $diaryId" }
                    ocrProcessor.extractText(photoUrl)
                }
                DiaryInputType.TEXT -> {
                    log.info("TEXT 타입: 입력된 텍스트 사용")
                    requireNotNull(diary.text) { "TEXT 타입 일기에 text가 누락되었습니다. ID: $diaryId" }
                }
            }

            val refinedText = textRefiner.refineText(baseText)

            val musicFuture = musicGenerator.generateMusicAsync(refinedText)
            val imageUrl = imageGenerator.generateImage(refinedText, diary.weather)

            val musicResult = musicFuture.get()

            val updateCommand = UpdateDiaryResultCommand.of(imageUrl, musicResult)
            diaryUpdater.updateResult(diaryId, updateCommand)

            log.info("비동기 일기 생성 완료 - ID: $diaryId")
        } catch (e: Exception) {
            log.error("일기 생성 중 에러 발생 - ID: $diaryId, 사유: ${e.message}")
            diaryFailureHandler.handle(diaryId, diary.user.id!!)
            notifySafely(diary, diaryId) { tokens -> notificationSender.sendDiaryFailure(tokens, diaryId) }
            return
        }

        notifySafely(diary, diaryId) { tokens -> notificationSender.sendDiaryCompletion(tokens, diaryId) }
    }

    private fun notifySafely(diary: Diary, diaryId: Long, send: (List<String>) -> List<String>) {
        if (!diary.user.isNotificationEnabled) return
        try {
            val tokens = fcmTokenReader.findAllByUserId(diary.user.id!!).map { it.token }
            val deadTokens = send(tokens)
            fcmTokenDeleter.deleteByTokens(deadTokens)
        } catch (e: Exception) {
            log.error("알림 발송 실패 - ID: $diaryId, 사유: ${e.message}")
        }
    }
}
