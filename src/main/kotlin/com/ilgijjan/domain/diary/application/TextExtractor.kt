package com.ilgijjan.domain.diary.application

import com.ilgijjan.integration.ocr.OcrProcessor
import org.springframework.stereotype.Component

@Component
class TextExtractor (
    private val ocrProcessor: OcrProcessor
){
    fun extractText(photoUrl: String?, text: String): String {
        return photoUrl?.let { ocrProcessor.extractText(it) } ?: text
    }
}