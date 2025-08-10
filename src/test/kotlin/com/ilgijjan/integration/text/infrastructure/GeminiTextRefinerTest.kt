package com.ilgijjan.integration.text.infrastructure

import com.ilgijjan.fixtures.DiaryTextFixtures
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class GeminiTextRefinerTest @Autowired constructor(
    private val geminiTextRefiner: GeminiTextRefiner
) {

    @Test
    fun refineText() {
        val rawText = DiaryTextFixtures.SAMPLE1

        val refinedText = geminiTextRefiner.refineText(rawText)

        println("Refined Text: $refinedText")

        assert(refinedText.isNotBlank()) { "정제된 텍스트가 비어있습니다." }
    }
}