package com.tadadiary.common.integration.image

import com.tadadiary.diary.domain.Weather

interface ImageGenerator {
    fun generateImage(text: String, weather: Weather): String
}