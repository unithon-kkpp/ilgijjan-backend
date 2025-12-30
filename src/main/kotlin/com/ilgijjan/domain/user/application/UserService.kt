package com.ilgijjan.domain.user.application

import com.ilgijjan.domain.user.domain.Character
import com.ilgijjan.domain.user.presentation.ReadMeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userReader: UserReader,
    private val userUpdater: UserUpdater
) {
    fun getMe(userId: Long): ReadMeResponse {
        val user = userReader.getUserById(userId)
        return ReadMeResponse.from(user)
    }

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
