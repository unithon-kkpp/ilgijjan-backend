package com.ilgijjan.integration.ocr.infrastructure

import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@Disabled
@SpringBootTest
class GoogleVisionOcrProcessorTest @Autowired constructor(
    private val googleVisionOcrProcessor: GoogleVisionOcrProcessor
) {

    @Test
    fun extractText() {
        val testImageUrl = "https://storage.googleapis.com/ilgijjan-bucket/123.jpg"

        val extractedText = googleVisionOcrProcessor.extractText(testImageUrl)

        println("Extracted OCR Text: $extractedText")

        assert(extractedText.isNotBlank()) { "OCR 결과가 비어있습니다. (이미지 인식 실패 또는 텍스트 없음)" }
    }
}