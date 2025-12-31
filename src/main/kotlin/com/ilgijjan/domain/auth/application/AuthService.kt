package com.ilgijjan.domain.auth.application

import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.common.jwt.TokenType
import com.ilgijjan.domain.auth.presentation.LoginRequest
import com.ilgijjan.domain.auth.presentation.LoginResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val socialUserProvider: SocialUserProvider,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Transactional
    fun login(request: LoginRequest): LoginResponse {

        val command = OauthCommand.from(request)
        val user = socialUserProvider.getOrCreateUser(command)

        val accessToken = jwtTokenProvider.createToken(user.id!!, TokenType.ACCESS)
        val refreshToken = jwtTokenProvider.createToken(user.id, TokenType.REFRESH)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isOnboarded = user.isOnboarded()
        )
    }
}
