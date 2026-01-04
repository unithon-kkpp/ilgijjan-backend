package com.ilgijjan.domain.auth.presentation

import com.ilgijjan.domain.auth.domain.OauthProvider

data class LoginRequest (
    override val provider: OauthProvider,
    override val accessToken: String?
) : OauthRequest
