package com.ilgijjan.domain.user.presentation

import com.ilgijjan.domain.user.domain.Character
import com.ilgijjan.domain.user.domain.User
import io.swagger.v3.oas.annotations.media.Schema

data class ReadMeResponse(
    @field:Schema(description = "이름", example = "짱구")
    val name: String?,

    @field:Schema(description = "캐릭터", nullable = false, example = "DODO")
    val character: Character?,

    @field:Schema(description = "알림 수신 여부", example = "true")
    val isNotificationEnabled: Boolean,

    @field:Schema(description = "음표 개수", example = "10")
    val noteCount: Int
) {
    companion object {
        fun from(user: User, noteCount: Int): ReadMeResponse {
            return ReadMeResponse(
                name = user.name,
                character = user.character,
                isNotificationEnabled = user.isNotificationEnabled,
                noteCount = noteCount
            )
        }
    }
}
