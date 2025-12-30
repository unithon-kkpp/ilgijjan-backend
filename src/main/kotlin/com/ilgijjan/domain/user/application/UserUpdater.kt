package com.ilgijjan.domain.user.application

import com.ilgijjan.domain.user.domain.Character
import org.springframework.stereotype.Component

@Component
class UserUpdater(
    private val userReader: UserReader
) {
    fun updateName(userId: Long, name: String) {
        val user = userReader.getUserById(userId)
        user.updateName(name)
    }

    fun updateCharacter(userId: Long, character: Character) {
        val user = userReader.getUserById(userId)
        user.updateCharacter(character)
    }
}
