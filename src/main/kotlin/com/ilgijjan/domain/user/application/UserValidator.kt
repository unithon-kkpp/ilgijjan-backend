package com.ilgijjan.domain.user.application

import com.ilgijjan.common.constants.UserConstants
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userRepository: UserRepository
) {
    fun validateName(userId: Long, name: String) {
        if (name.startsWith(UserConstants.TEMPORARY_NAME_PREFIX)) {
            throw CustomException(ErrorCode.INVALID_NAME_FORMAT)
        }

        if (userRepository.existsByNameAndIdNot(name, userId)) {
            throw CustomException(ErrorCode.DUPLICATE_NAME)
        }
    }
}
