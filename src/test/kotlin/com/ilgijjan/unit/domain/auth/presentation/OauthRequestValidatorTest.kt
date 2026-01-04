package com.ilgijjan.unit.domain.auth.presentation

import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.auth.presentation.LoginRequest
import com.ilgijjan.domain.auth.presentation.OauthRequestValidator
import jakarta.validation.ConstraintValidatorContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock

class OauthRequestValidatorTest {
    private lateinit var validator: OauthRequestValidator
    private val context = mock(ConstraintValidatorContext::class.java, RETURNS_DEEP_STUBS)

    @BeforeEach
    fun setUp() {
        validator = OauthRequestValidator()
    }

    @Test
    fun `카카오 로그인 시 accessToken이 없으면 실패한다`() {
        // given
        val request = LoginRequest(
            provider = OauthProvider.KAKAO,
            accessToken = null
        )

        // when
        val result = validator.isValid(request, context)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `카카오 로그인 시 accessToken이 공백이면 실패한다`() {
        // given
        val request = LoginRequest(
            provider = OauthProvider.KAKAO,
            accessToken = "  "
        )

        // when
        val result = validator.isValid(request, context)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `카카오 로그인 시 accessToken이 존재하면 성공한다`() {
        // given
        val request = LoginRequest(
            provider = OauthProvider.KAKAO,
            accessToken = "valid_access_token"
        )

        // when
        val result = validator.isValid(request, context)

        // then
        assertThat(result).isTrue()
    }
}
