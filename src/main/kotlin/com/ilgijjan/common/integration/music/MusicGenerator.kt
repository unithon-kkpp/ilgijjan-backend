package com.ilgijjan.common.integration.music

interface MusicGenerator {
    fun generateMusic(text: String): MusicResult
}