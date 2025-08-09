package com.ilgijjan.common.integration.music

import org.springframework.stereotype.Component

@Component
class SunoMusicGenerator : MusicGenerator {
    override fun generateMusic(text: String): Pair<String, String> {
        // suno API 호출
        return "https://example.com/generated_audio.mp4" to "lyrics"
    }
}