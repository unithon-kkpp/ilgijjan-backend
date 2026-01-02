package com.ilgijjan.common.aop

import com.ilgijjan.common.annotation.LogExecutionTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Aspect
@Component
class ExecutionTimeAspect {
    private val log = LoggerFactory.getLogger(javaClass)

    @Around("@annotation(logExecutionTime)")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint, logExecutionTime: LogExecutionTime): Any? {
        val stopWatch = StopWatch()

        stopWatch.start()
        try {
            return joinPoint.proceed()
        } finally {
            if (stopWatch.isRunning) {
                stopWatch.stop()
            }

            val className = joinPoint.signature.declaringType.simpleName
            val methodName = joinPoint.signature.name
            val taskTime = stopWatch.totalTimeSeconds

            log.info("⏱️ Execution Time: [$className.$methodName] - ${taskTime}s")
        }
    }
}
