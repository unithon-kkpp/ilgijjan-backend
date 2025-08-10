package com.ilgijjan.integration.image

import com.ilgijjan.domain.diary.domain.Weather
import org.springframework.stereotype.Component

@Component
class ReplicateImageGenerator : ImageGenerator {
    override fun generateImage(text: String, weather: Weather): String {
        // Replicate + Stable Diffusion API 호출
        return "https://example.com/generated_image.png"
    }
}