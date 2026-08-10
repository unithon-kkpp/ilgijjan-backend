package com.ilgijjan.domain.diary.application

import com.ilgijjan.domain.diary.domain.DiaryStatus
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DiaryRecoveryRunner(
    private val diaryReader: DiaryReader,
    private val diaryTaskProcessor: DiaryTaskProcessor
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        val pendingDiaries = diaryReader.findAllByStatus(DiaryStatus.PENDING)
        if (pendingDiaries.isEmpty()) return

        log.info("크래시 복구: PENDING 일기 ${pendingDiaries.size}건 재처리 시작")
        pendingDiaries.forEach { diary ->
            diaryTaskProcessor.process(diary.id!!)
        }
    }
}
