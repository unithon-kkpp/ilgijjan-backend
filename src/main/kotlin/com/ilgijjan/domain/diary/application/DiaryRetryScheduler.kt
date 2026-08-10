package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.constants.DiaryConstants
import com.ilgijjan.domain.diary.domain.DiaryStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DiaryRetryScheduler(
    private val diaryReader: DiaryReader,
    private val diaryUpdater: DiaryUpdater,
    private val diaryTaskProcessor: DiaryTaskProcessor
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0/10 * * * *", zone = "Asia/Seoul")
    fun retryFailedDiaries() {
        val failedDiaries = diaryReader.findAllByStatus(DiaryStatus.FAILED)
        if (failedDiaries.isEmpty()) return

        log.info("배치 재시도: FAILED 일기 ${failedDiaries.size}건 검사")

        failedDiaries.forEach { diary ->
            val diaryId = diary.id!!
            if (diary.retryCount >= DiaryConstants.MAX_RETRY_COUNT) {
                log.info("배치 재시도: 최대 재시도 횟수 초과, 영구 실패 처리 - ID: $diaryId")
                diaryUpdater.failPermanently(diaryId)
            } else {
                log.info("배치 재시도: 재처리 시작 - ID: $diaryId, 시도 횟수: ${diary.retryCount + 1}")
                diaryUpdater.increaseRetryCount(diaryId)
                diaryUpdater.markPending(diaryId)
                diaryTaskProcessor.process(diaryId)
            }
        }
    }
}
