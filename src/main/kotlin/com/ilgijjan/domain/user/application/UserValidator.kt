package com.ilgijjan.domain.user.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userRepository: UserRepository
) {
    fun validateDuplicateName(userId: Long, name: String) {
        if (userRepository.existsByNameAndIdNot(name, userId)) {
            throw CustomException(ErrorCode.DUPLICATE_NAME)
        }
    }
}
