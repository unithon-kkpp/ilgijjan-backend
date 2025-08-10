package com.ilgijjan.integration.image

import com.ilgijjan.domain.diary.domain.Weather
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
class ImageController(
    private val imageGenerator: ImageGenerator
) {
    @GetMapping("/api/image/generate")
    fun generateImage(@RequestParam text: String, @RequestParam weather: Weather): Map<String, String> {
        val imageUrl = imageGenerator.generateImage(text, weather)
        return mapOf("imageUrl" to imageUrl)
    }
}