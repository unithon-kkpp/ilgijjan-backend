package com.ilgijjan.domain.auth.application

import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.common.jwt.TokenType
import com.ilgijjan.domain.auth.presentation.LoginRequest
import com.ilgijjan.domain.auth.presentation.LoginResponse
import com.ilgijjan.domain.auth.presentation.LogoutRequest
import com.ilgijjan.domain.auth.presentation.WithdrawRequest
import com.ilgijjan.domain.user.application.UserDeleter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val socialUserProcessor: SocialUserProcessor,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDeleter: UserDeleter
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

    @Transactional
    fun withdraw(userId: Long, request: WithdrawRequest) {
        userDeleter.deleteById(userId)
        socialUserProcessor.unlink(OauthCommand.from(request))
        // TODO: 일기짠 토큰 무효화
    }
}
