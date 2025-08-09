package com.ilgijjan.common.integration.music

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/api/music")
class MusicGenerateController(
    private val musicGenerator: MusicGenerator
) {
    @PostMapping("/generate")
    fun generate(@RequestBody request: GenerateRequest): ResponseEntity<GenerateResponse> {
        return try {
            val (audioUrl, lyrics) = musicGenerator.generateMusic(request.text)
            ResponseEntity.ok(GenerateResponse(audioUrl, lyrics))
        } catch (ex: Exception) {
            ResponseEntity.badRequest().body(GenerateResponse("", "Error: ${ex.message}"))
        }
    }
}

data class GenerateRequest(
    val text: String
)

data class GenerateResponse(
    val audioUrl: String,
    val lyrics: String
)