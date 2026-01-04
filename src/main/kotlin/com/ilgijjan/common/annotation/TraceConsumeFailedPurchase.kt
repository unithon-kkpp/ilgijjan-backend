package com.ilgijjan.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TraceConsumeFailedPurchase(
    val value: String = "purchaseToken"
)