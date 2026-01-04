package com.ilgijjan.unit.domain.billing.presentation

import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.domain.billing.presentation.BillingRequestValidator
import com.ilgijjan.domain.billing.presentation.BillingVerifyRequest
import jakarta.validation.ConstraintValidatorContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class BillingRequestValidatorTest {
    private lateinit var validator: BillingRequestValidator
    private val context = mock(ConstraintValidatorContext::class.java, RETURNS_DEEP_STUBS)

    @BeforeEach
    fun setUp() {
        validator = BillingRequestValidator()
    }

    @Test
    fun `원스토어 결제 검증 시 purchaseToken이 없으면 실패한다`() {
        // given
        val request = BillingVerifyRequest(StoreType.ONE_STORE, "prod_id", null)

        // when
        val result = validator.isValid(request, context)

        // then
        assertThat(result).isFalse()
    }
}
