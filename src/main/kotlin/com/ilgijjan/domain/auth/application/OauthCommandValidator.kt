package com.ilgijjan.domain.auth.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.auth.domain.OauthProvider
import org.springframework.stereotype.Component

@Component
class OauthCommandValidator {
    fun validate(command: OauthCommand) {
        when (command.provider) {
            OauthProvider.KAKAO -> {
                if (command.accessToken.isNullOrBlank()) {
                    throw CustomException(ErrorCode.KAKAO_REQUIRED_FIELD_MISSING)
                }
            }
        }
    }
}