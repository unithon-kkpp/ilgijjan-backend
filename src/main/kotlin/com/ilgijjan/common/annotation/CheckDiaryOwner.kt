package com.ilgijjan.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckDiaryOwner(
    val value: String = "diaryId"
)
