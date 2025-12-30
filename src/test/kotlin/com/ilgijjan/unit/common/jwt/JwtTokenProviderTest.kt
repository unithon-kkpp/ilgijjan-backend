package com.ilgijjan.unit.common.jwt

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.common.jwt.TokenType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class JwtTokenProviderTest {

    companion object {
        private const val TEST_SECRET_KEY =
            "v3S6v9yBEHMcQfTjWmZq4t7wzCFJaNdRgUkXp2s5u8xA2DnGKbPeShVmYp3s6v9y"
    }

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider(TEST_SECRET_KEY)
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
        val invalidToken = "eyJhbGciOiJIUzI1NiJ9.wrong.payload"

        // when & then
        val exception = assertThrows<CustomException> {
            jwtTokenProvider.validateToken(invalidToken)
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_TOKEN)
    }

    @Test
    fun `getAuthentication - 토큰을 해석해서 유저 ID를 정확히 가져온다`() {
        // given
        val expectedUserId = 33L
        val token = jwtTokenProvider.createToken(expectedUserId, TokenType.ACCESS)

        // when
        val authentication = jwtTokenProvider.getAuthentication(token)

        // then
        assertThat(authentication.principal).isEqualTo(expectedUserId)
        assertThat(authentication.authorities.map { it.authority }).contains("ROLE_USER")    }
}
