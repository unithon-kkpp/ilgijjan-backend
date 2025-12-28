package com.ilgijjan.domain.diary.application

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CreateDiaryEventHandler(
    private val diaryTaskProcessor: DiaryTaskProcessor
) {
    @EventListener
    fun handle(event: CreateDiaryEvent) {
        diaryTaskProcessor.process(event.diaryId)
    }
}
