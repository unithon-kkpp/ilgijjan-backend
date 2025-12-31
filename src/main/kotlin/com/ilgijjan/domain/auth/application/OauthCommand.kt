package com.ilgijjan.domain.auth.application

import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.auth.presentation.LoginRequest

data class OauthCommand(
    val provider: OauthProvider,
    val accessToken: String?
) {
    companion object {
        fun from(request: LoginRequest): OauthCommand {
            return OauthCommand(
                provider = request.provider,
                accessToken = request.accessToken
            )
        }
    }
}