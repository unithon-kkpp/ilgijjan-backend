package com.ilgijjan.integration.image.application

import com.ilgijjan.domain.diary.domain.Weather
import java.util.concurrent.CompletableFuture

interface ImageGenerator {
    fun generateImageAsync(text: String, weather: Weather): CompletableFuture<String>
}