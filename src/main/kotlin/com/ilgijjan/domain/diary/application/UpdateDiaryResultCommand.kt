package com.ilgijjan.domain.diary.application

import com.ilgijjan.integration.music.application.MusicResult

data class UpdateDiaryResultCommand(
    val refinedText: String,
    val imageUrl: String,
    val musicUrl: String,
    val lyrics: String
) {
    companion object {
        fun of(refinedText: String, imageUrl: String, musicResult: MusicResult): UpdateDiaryResultCommand {
            return UpdateDiaryResultCommand(
                refinedText = refinedText,
                imageUrl = imageUrl,
                musicUrl = musicResult.audioUrl,
                lyrics = musicResult.lyrics
            )
        }
    }
}
