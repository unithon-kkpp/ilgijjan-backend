package com.ilgijjan.domain.diary.application

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DiaryEventListener(
    private val diaryTaskProcessor: DiaryTaskProcessor
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun
            onDiaryCreated(event: DiaryCreatedEvent) {
        diaryTaskProcessor.process(event.diaryId)
    }
}
