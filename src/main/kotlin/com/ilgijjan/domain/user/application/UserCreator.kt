package com.ilgijjan.domain.user.application

import com.ilgijjan.common.constants.UserConstants
import com.ilgijjan.domain.auth.domain.OauthInfo
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.domain.Character
import com.ilgijjan.domain.user.domain.User
import com.ilgijjan.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component

@Component
class UserCreator(
    private val userRepository: UserRepository
) {
    fun createSocialUser(provider: OauthProvider, providerId: String): User {
        val tempNickname = "${UserConstants.TEMPORARY_NAME_PREFIX}${providerId.take(8)}"
        val newUser = User(
            name = tempNickname,
            character = Character.DODO,
            oauthInfo = OauthInfo(
                provider = provider,
                providerId = providerId
            )
        )
        return userRepository.save(newUser)
    }
}
