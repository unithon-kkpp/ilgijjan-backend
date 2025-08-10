package com.ilgijjan.integration.music.application

interface MusicGenerator {
    fun generateMusic(text: String): MusicResult
}