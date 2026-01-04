package com.ilgijjan.domain.auth.presentation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [OauthRequestValidator::class])
annotation class ValidOauthRequest(
    val message: String = "부적절한 OAuth 요청입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
