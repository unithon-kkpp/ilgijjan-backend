package com.ilgijjan.domain.auth.application

import com.ilgijjan.domain.user.application.UserCreator
import com.ilgijjan.domain.user.application.UserReader
import com.ilgijjan.domain.user.domain.User
import com.ilgijjan.integration.oauth.application.OauthClient
import com.ilgijjan.integration.oauth.application.OauthClients
import org.springframework.stereotype.Component

@Component
class SocialUserProcessor(
    private val oauthClients: OauthClients,
    private val userReader: UserReader,
    private val userCreator: UserCreator,

) {
    fun getOrCreateUser(command: OauthCommand): User {
        val client = oauthClients.getClient(command.provider)
        val providerId = client.getProviderId(command)

        return userReader.findByProviderId(command.provider, providerId)
            ?: userCreator.createSocialUser(command.provider, providerId)
    }

    fun logout(command: OauthCommand) {
        val client = oauthClients.getClient(command.provider)
        client.logout(command)
    }

    fun unlink(command: OauthCommand) {
        val client = oauthClients.getClient(command.provider)
        client.unlink(command)
    }
}
