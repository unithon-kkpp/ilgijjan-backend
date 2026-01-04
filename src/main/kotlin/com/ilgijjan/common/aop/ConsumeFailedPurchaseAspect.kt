package com.ilgijjan.common.aop

import com.ilgijjan.common.annotation.TraceConsumeFailedPurchase
import com.ilgijjan.domain.billing.application.PaymentHistoryUpdater
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ConsumeFailedPurchaseAspect(
    private val paymentHistoryUpdater: PaymentHistoryUpdater
){
    private val log = LoggerFactory.getLogger(this::class.java)

    @Around("@annotation(markConsumeFailedPurchase)")
    fun handleLogging(joinPoint: ProceedingJoinPoint, markConsumeFailedPurchase: TraceConsumeFailedPurchase): Any? {
        return try {
            joinPoint.proceed()
        } catch (e: Exception) {
            val purchaseToken = getPurchaseToken(joinPoint, markConsumeFailedPurchase.value)
            paymentHistoryUpdater.consumeFail(purchaseToken)
            log.error("[Consume Fail] 구매 토큰: $purchaseToken | 메시지: ${e.message}", e)
            null
        }
    }

    private fun getPurchaseToken(joinPoint: ProceedingJoinPoint, paramName: String): String {
        val signature = joinPoint.signature as MethodSignature
        val index = signature.parameterNames.indexOf(paramName)
        if (index == -1) throw IllegalArgumentException("[TraceConsumeFailedPurchase]: '$paramName' 파라미터를 찾을 수 없습니다.")
        val tokenArg = joinPoint.args[index]
        require(tokenArg is String) { "[TraceConsumeFailedPurchase]: '$paramName' must be String" }
        return tokenArg
    }
}
