package com.ilgijjan.domain.auth.presentation

import com.ilgijjan.domain.auth.domain.OauthProvider
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class OauthRequestValidator : ConstraintValidator<ValidOauthRequest, OauthRequest> {

    override fun isValid(request: OauthRequest, context: ConstraintValidatorContext): Boolean {
        return when (request.provider) {
            OauthProvider.KAKAO -> {
                if (request.accessToken.isNullOrBlank()) {
                    addViolation(context, "카카오 요청은 accessToken이 필수입니다.", "accessToken")
                    return false
                }
                true
            }
        }
    }

    private fun addViolation(context: ConstraintValidatorContext, message: String, property: String) {
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(property)
            .addConstraintViolation()
    }
}
