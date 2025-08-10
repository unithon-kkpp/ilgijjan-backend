package com.ilgijjan.integration.storage.presentation

import io.swagger.v3.oas.annotations.media.Schema

data class StorageResponse (
    @field:Schema(description = "파일 url", example = "https://example.com/photo.jpg")
    val fileUrl: String
)
