package com.ilgijjan.domain.billing.presentation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [BillingRequestValidator::class])
annotation class ValidBillingRequest(
    val message: String = "부적절한 결제 요청입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
