package com.ilgijjan.domain.user.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserDeleter(
    private val userReader: UserReader
) {
    @Transactional
    fun deleteById(userId: Long) {
        val user = userReader.getUserById(userId)
        user.withdraw()
    }
}
