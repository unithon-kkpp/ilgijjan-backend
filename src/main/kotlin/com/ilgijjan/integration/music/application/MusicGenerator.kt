package com.ilgijjan.integration.music.application

import java.util.concurrent.CompletableFuture

interface MusicGenerator {
    fun generateMusicAsync(text: String): CompletableFuture<MusicResult>
}