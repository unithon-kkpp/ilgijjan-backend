package com.ilgijjan.domain.user.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.domain.User
import com.ilgijjan.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component

@Component
class UserReader(
    private val userRepository: UserRepository
) {
    fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
    }

    fun findByProviderId(provider: OauthProvider, providerId: String): User? {
        return userRepository.findByOauthInfoProviderAndOauthInfoProviderId(provider, providerId)
    }
}
