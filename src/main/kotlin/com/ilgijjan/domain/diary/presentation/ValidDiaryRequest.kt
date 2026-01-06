package com.ilgijjan.domain.diary.presentation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DiaryRequestValidator::class])
annotation class ValidDiaryRequest(
    val message: String = "일기 생성 요청이 부적절합니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
