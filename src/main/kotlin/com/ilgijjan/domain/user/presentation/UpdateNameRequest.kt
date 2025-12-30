package com.ilgijjan.domain.user.presentation

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class UpdateNameRequest(
    @field:NotBlank
    @field:Schema(description = "이름", nullable = false, example = "짱구")
    val name: String
)
