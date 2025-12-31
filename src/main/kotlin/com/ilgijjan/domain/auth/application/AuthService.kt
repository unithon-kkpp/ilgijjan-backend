package com.ilgijjan.domain.auth.application

import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.common.jwt.TokenType
import com.ilgijjan.domain.auth.presentation.LoginRequest
import com.ilgijjan.domain.auth.presentation.LoginResponse
import com.ilgijjan.domain.auth.presentation.LogoutRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val socialUserProcessor: SocialUserProcessor,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        val user = socialUserProcessor.getOrCreateUser(OauthCommand.from(request))

        val accessToken = jwtTokenProvider.createToken(user.id!!, TokenType.ACCESS)
        val refreshToken = jwtTokenProvider.createToken(user.id, TokenType.REFRESH)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isOnboarded = user.isOnboarded()
        )
    }

    @Transactional
    fun logout(userId: Long, refreshToken: String, request: LogoutRequest) {
        socialUserProcessor.logout(OauthCommand.from(request))
        // TODO: 일기짠 토큰 무효화
    }
}
