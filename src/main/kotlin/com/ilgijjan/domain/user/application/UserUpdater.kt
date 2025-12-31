package com.ilgijjan.domain.user.application

import com.ilgijjan.domain.user.domain.Character
import org.springframework.stereotype.Component

@Component
class UserUpdater(
    private val userReader: UserReader,
    private val userValidator: UserValidator
) {
    fun updateName(userId: Long, name: String) {
        userValidator.validateName(userId, name)
        val user = userReader.getUserById(userId)
        user.updateName(name)
    }

    fun updateCharacter(userId: Long, character: Character) {
        val user = userReader.getUserById(userId)
        user.updateCharacter(character)
    }

    fun updateNotification(userId: Long, isEnabled: Boolean) {
        val user = userReader.getUserById(userId)
        user.updateNotification(isEnabled)
    }
}
