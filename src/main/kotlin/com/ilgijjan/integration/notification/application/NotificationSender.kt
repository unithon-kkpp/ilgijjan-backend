package com.ilgijjan.integration.notification.application

interface NotificationSender {
    fun sendDiaryCompletion(tokens: List<String>, diaryId: Long): List<String>
}
