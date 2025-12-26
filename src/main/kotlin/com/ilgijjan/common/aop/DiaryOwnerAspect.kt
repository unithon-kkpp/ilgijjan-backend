package com.ilgijjan.common.aop

import com.ilgijjan.common.annotation.CheckDiaryOwner
import com.ilgijjan.common.utils.SecurityUtil
import com.ilgijjan.domain.diary.application.DiaryReader
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class DiaryOwnerAspect(
    private val diaryReader: DiaryReader
) {
    @Before("@annotation(checkDiaryOwner)")
    fun checkOwner(joinPoint: JoinPoint, checkDiaryOwner: CheckDiaryOwner) {
        val currentUserId = SecurityUtil.getCurrentUserId()
        val diaryId = getDiaryId(joinPoint, checkDiaryOwner.value)
        val diary = diaryReader.getDiaryById(diaryId)
        diary.validateOwner(currentUserId)
    }

    private fun getDiaryId(joinPoint: JoinPoint, paramName: String): Long {
        val signature = joinPoint.signature as MethodSignature
        val index = signature.parameterNames.indexOf(paramName)
        if (index == -1) throw IllegalArgumentException("[CheckDiaryOwner]: '$paramName' 파라미터를 찾을 수 없습니다.")
        val diaryIdArg = joinPoint.args[index]
        require(diaryIdArg is Long) { "[CheckDiaryOwner] '$paramName' must be Long" }
        return diaryIdArg
    }
}
