package com.ilgijjan.domain.auth.presentation

import com.ilgijjan.domain.auth.domain.OauthProvider

data class LogoutRequest (
    override val provider: OauthProvider,
    override val accessToken: String
) : OauthRequest
