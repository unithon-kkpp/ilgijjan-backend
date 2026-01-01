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
    private val oauthCommandValidator: OauthCommandValidator,
    private val userReader: UserReader,
    private val userCreator: UserCreator
) {
    fun getOrCreateUser(command: OauthCommand): User {
        val client = getValidatedClient(command)
        val providerId = client.getProviderId(command)

        return userReader.findByProviderId(command.provider, providerId)
            ?: userCreator.createSocialUser(command.provider, providerId)
    }

    fun logout(command: OauthCommand) {
        val client = getValidatedClient(command)
        client.logout(command)
    }

    fun unlink(command: OauthCommand) {
        val client = getValidatedClient(command)
        client.unlink(command)
    }

    private fun getValidatedClient(command: OauthCommand): OauthClient {
        oauthCommandValidator.validate(command)
        return oauthClients.getClient(command.provider)
    }
}
