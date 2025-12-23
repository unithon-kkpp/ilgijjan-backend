package com.ilgijjan.common.jwt

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JwtTokenProviderTest @Autowired constructor(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Test
    @Disabled("개발 시 임시 토큰이 필요할 때만 수동으로 실행")
    fun `개발용 토큰 발급`() {
        // given
        val userId = 1L

        // when
        val accessToken = jwtTokenProvider.createToken(userId, TokenType.ACCESS)
        val refreshToken = jwtTokenProvider.createToken(userId, TokenType.REFRESH)

        // then
        println("Access Token: $accessToken")
        println("Refresh Token: $refreshToken")
    }

    @Test
    fun `토큰 생성 및 유효성 검증 성공`() {
        // given
        val userId = 1L
        val token = jwtTokenProvider.createToken(userId, TokenType.ACCESS)

        // when & then
        assertDoesNotThrow {
            jwtTokenProvider.validateToken(token)
        }
    }

    @Test
    fun `유효하지 않은 토큰이면 INVALID_TOKEN 예외가 터진다`() {
        // given
        val invalidToken = "..."

        // when & then
        val exception = assertThrows<CustomException> {
            jwtTokenProvider.validateToken(invalidToken)
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_TOKEN)
    }

    @Test
    fun `getAuthentication - 토큰을 해석해서 유저 ID와 권한을 정확히 가져온다`() {
        // given
        val expectedUserId = 33L
        val token = jwtTokenProvider.createToken(expectedUserId, TokenType.ACCESS)

        // when
        val authentication = jwtTokenProvider.getAuthentication(token)
        val roles = authentication.authorities.map { it.authority }

        // then
        assertThat(authentication.principal).isEqualTo(expectedUserId)
        assertThat(roles).contains("ROLE_USER")
    }
}
