package com.ilgijjan.integration.image.application

import com.ilgijjan.domain.diary.domain.Weather

interface ImageGenerator {
    fun generateImage(text: String, weather: Weather): String
}