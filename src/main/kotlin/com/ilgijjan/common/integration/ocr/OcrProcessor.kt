package com.ilgijjan.common.integration.ocr

interface OcrProcessor {
    fun extractText(photoUrl: String): String
}