package com.ilgijjan.domain.auth.application

import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.auth.presentation.LoginRequest
import com.ilgijjan.domain.auth.presentation.LogoutRequest
import com.ilgijjan.domain.auth.presentation.WithdrawRequest

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

        fun from(request: LogoutRequest): OauthCommand {
            return OauthCommand(
                provider = request.provider,
                accessToken = request.accessToken
            )
        }

        fun from(request: WithdrawRequest): OauthCommand {
            return OauthCommand(
                provider = request.provider,
                accessToken = request.accessToken
            )
        }
    }
}
