package com.ilgijjan.integration.ocr

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class OcrController(
    private val ocrProcessor: OcrProcessor
) {

    @GetMapping("/api/ocr/extract")
    fun extractText(@RequestParam photoUrl: String): String {
        return ocrProcessor.extractText(photoUrl)
    }
}