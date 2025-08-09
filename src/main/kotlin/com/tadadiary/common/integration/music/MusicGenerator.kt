package com.tadadiary.common.integration.music

interface MusicGenerator {
    fun generateMusic(text: String): Pair<String, String> // <mp4Url, lyrics>
}