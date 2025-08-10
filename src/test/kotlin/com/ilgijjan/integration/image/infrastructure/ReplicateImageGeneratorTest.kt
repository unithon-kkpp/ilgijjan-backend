package com.ilgijjan.integration.image.infrastructure

import com.ilgijjan.domain.diary.domain.Weather
import com.ilgijjan.fixtures.DiaryTextFixtures
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class ReplicateImageGeneratorIntegrationTest @Autowired constructor(
    private val replicateImageGenerator: ReplicateImageGenerator
) {

    @Test
    fun generateImage() {
        val text = DiaryTextFixtures.SAMPLE1
        val weather = Weather.SUNNY

        val imageUrl = replicateImageGenerator.generateImage(text, weather)

        println("Generated Image URL: $imageUrl")

        assert(imageUrl.isNotBlank()) { "이미지 URL이 비어있습니다." }
    }
}