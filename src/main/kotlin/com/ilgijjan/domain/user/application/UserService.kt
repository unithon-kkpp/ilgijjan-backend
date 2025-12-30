package com.ilgijjan.domain.user.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userReader: UserReader,
    private val userUpdater: UserUpdater
) {
    @Transactional
    fun updateName(userId: Long, newName: String) {
        userUpdater.updateName(userId, newName)
    }
}
