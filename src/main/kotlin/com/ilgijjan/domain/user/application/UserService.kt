package com.ilgijjan.domain.user.application

import com.ilgijjan.domain.user.domain.Character
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userUpdater: UserUpdater
) {
    @Transactional
    fun updateName(userId: Long, newName: String) {
        userUpdater.updateName(userId, newName)
    }

    @Transactional
    fun updateCharacter(userId: Long, character: Character) {
        userUpdater.updateCharacter(userId, character)
    }

    @Transactional
    fun updateNotification(userId: Long, isEnabled: Boolean) {
        userUpdater.updateNotification(userId, isEnabled)
    }
}
