package com.ilgijjan.integration.ocr

interface OcrProcessor {
    fun extractText(photoUrl: String): String
}