package com.ilgijjan.unit.domain.billing.presentation

import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.domain.billing.presentation.BillingRequestValidator
import com.ilgijjan.domain.billing.presentation.BillingVerifyRequest
import jakarta.validation.ConstraintValidatorContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

class BillingRequestValidatorTest {
    private lateinit var validator: BillingRequestValidator
    private lateinit var context: ConstraintValidatorContext

    @BeforeEach
    fun setUp() {
        validator = BillingRequestValidator()
        context = mock(ConstraintValidatorContext::class.java)

        val builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder::class.java)
        val nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext::class.java)

        `when`(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder)
        `when`(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder)
    }

    @Test
    fun `원스토어 영수증에 purchaseToken이 없으면 isValid는 false를 반환한다`() {
        val request = BillingVerifyRequest(StoreType.ONE_STORE, "prod_id", null)
        val result = validator.isValid(request, context)
        assertThat(result).isFalse()
    }
}
