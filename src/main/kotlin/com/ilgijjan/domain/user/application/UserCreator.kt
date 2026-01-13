package com.ilgijjan.domain.user.application

import com.ilgijjan.common.constants.WalletConstants
import com.ilgijjan.domain.auth.domain.OauthInfo
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.wallet.domain.UserWallet
import com.ilgijjan.domain.wallet.infrastructure.UserWalletRepository
import com.ilgijjan.domain.user.domain.User
import com.ilgijjan.domain.user.infrastructure.UserRepository
import org.springframework.stereotype.Component

@Component
class UserCreator(
    private val userRepository: UserRepository,
    private val walletRepository: UserWalletRepository
) {
    fun createSocialUser(provider: OauthProvider, providerId: String): User {

        val newUser = User(
            oauthInfo = OauthInfo(
                provider = provider,
                providerId = providerId
            )
        )

        val savedUser = userRepository.save(newUser)
        walletRepository.save(UserWallet(userId = savedUser.id!!, noteCount = WalletConstants.INITIAL_NOTES))
        return savedUser
    }
}
