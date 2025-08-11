package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.integration.ocr.application.OcrProcessor
import org.springframework.stereotype.Component

@Component
class TextExtractor (
    private val ocrProcessor: OcrProcessor
){
    fun extractText(photoUrl: String?, text: String): String {
        if ((photoUrl == null && text.isEmpty()) || (photoUrl != null && text.isNotEmpty())) {
            throw CustomException(ErrorCode.INVALID_INPUT_FOR_DIARY)
        }
        return photoUrl?.let { ocrProcessor.extractText(it) } ?: text
    }
}