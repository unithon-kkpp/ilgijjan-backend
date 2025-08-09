package com.tadadiary.diary.application

import com.tadadiary.common.integration.ocr.OcrProcessor
import org.springframework.stereotype.Component

@Component
class TextExtractor (
    private val ocrProcessor: OcrProcessor
){
    fun extractText(photoUrl: String?, text: String): String {
        return photoUrl?.let { ocrProcessor.extractText(it) } ?: text
    }
}