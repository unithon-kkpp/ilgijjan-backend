package com.ilgijjan.domain.user.presentation

import com.ilgijjan.domain.user.domain.Character
import com.ilgijjan.domain.user.domain.User

data class ReadMeResponse(
    val name: String?,
    val character: Character?,
    val isNotificationEnabled: Boolean,
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
