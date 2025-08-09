package com.ilgijjan.common.integration.image

import com.ilgijjan.diary.domain.Weather

interface ImageGenerator {
    fun generateImage(text: String, weather: Weather): String
}