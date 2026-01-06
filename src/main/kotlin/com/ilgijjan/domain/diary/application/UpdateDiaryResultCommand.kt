package com.ilgijjan.domain.diary.application

import com.ilgijjan.integration.music.application.MusicResult

data class UpdateDiaryResultCommand(
    val imageUrl: String,
    val musicUrl: String,
    val lyrics: String
) {
    companion object {
        fun of(imageUrl: String, musicResult: MusicResult): UpdateDiaryResultCommand {
            return UpdateDiaryResultCommand(
                imageUrl = imageUrl,
                musicUrl = musicResult.audioUrl,
                lyrics = musicResult.lyrics
            )
        }
    }
}
