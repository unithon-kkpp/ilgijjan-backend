package com.ilgijjan.integration.ocr.application

interface OcrProcessor {
    fun extractText(photoUrl: String): String
}