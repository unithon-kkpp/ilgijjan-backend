package com.tadadiary.common.integration.ocr

interface OcrProcessor {
    fun extractText(photoUrl: String): String
}