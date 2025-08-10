package com.ilgijjan.integration.music

interface MusicGenerator {
    fun generateMusic(text: String): MusicResult
}