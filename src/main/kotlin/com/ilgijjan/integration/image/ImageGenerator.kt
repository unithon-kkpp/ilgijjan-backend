package com.ilgijjan.integration.image

import com.ilgijjan.domain.diary.domain.Weather

interface ImageGenerator {
    fun generateImage(text: String, weather: Weather): String
}