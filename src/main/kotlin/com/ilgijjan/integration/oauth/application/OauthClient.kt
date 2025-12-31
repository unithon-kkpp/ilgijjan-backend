package com.ilgijjan.integration.oauth.application

import com.ilgijjan.domain.auth.application.OauthCommand
import com.ilgijjan.domain.auth.domain.OauthProvider

interface OauthClient {
    fun supports(provider: OauthProvider): Boolean
    fun getProviderId(command: OauthCommand): String
}
