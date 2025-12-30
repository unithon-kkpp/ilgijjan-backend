package com.ilgijjan.domain.user.presentation

import com.ilgijjan.domain.user.domain.Character
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateCharacterRequest(
    @field:Schema(description = "캐릭터", nullable = false, example = "DODO")
    val character: Character
)