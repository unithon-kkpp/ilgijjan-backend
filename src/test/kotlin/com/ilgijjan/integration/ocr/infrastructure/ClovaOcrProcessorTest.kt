package com.ilgijjan.integration.ocr.infrastructure

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class ClovaOcrProcessorTest @Autowired constructor(
    private val clovaOcrProcessor: ClovaOcrProcessor
) {

    @Test
    fun extractText() {
        val testImageUrl = "https://storage.googleapis.com/kkpp-bucket/06d0882a-dc40-4173-a7c8-203e19d9ed82"

        val extractedText = clovaOcrProcessor.extractText(testImageUrl)

        println("Extracted OCR Text: $extractedText")

        assert(extractedText.isNotBlank()) { "OCR 결과가 비어있습니다." }
    }
}