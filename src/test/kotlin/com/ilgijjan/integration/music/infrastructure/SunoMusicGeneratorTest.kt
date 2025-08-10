package com.ilgijjan.integration.music.infrastructure

import com.ilgijjan.integration.music.application.MusicResult
import com.ilgijjan.fixtures.DiaryTextFixtures
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class SunoMusicGeneratorTest @Autowired constructor(
    private val sunoMusicGenerator: SunoMusicGenerator
) {

    @Test
    fun generateMusic() {
        val text = DiaryTextFixtures.SAMPLE1
        val result: MusicResult = sunoMusicGenerator.generateMusic(text)

        println("Generated music audio URL: ${result.audioUrl}")
        println("Generated lyrics: ${result.lyrics}")

        assert(result.audioUrl.isNotBlank()) { "오디오 URL이 비어있습니다." }
        assert(result.lyrics.isNotBlank()) { "가사가 비어있습니다." }
    }
}