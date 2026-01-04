package com.ilgijjan.domain.billing.presentation

import com.ilgijjan.domain.billing.domain.StoreType
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class BillingRequestValidator : ConstraintValidator<ValidBillingRequest, BillingVerifyRequest> {

    override fun isValid(value: BillingVerifyRequest, context: ConstraintValidatorContext): Boolean {
        if (value.storeProductId.isBlank()) {
            addViolation(context, "storeProductId는 필수입니다.", "storeProductId")
            return false
        }

        return when (value.storeType) {
            StoreType.ONE_STORE, StoreType.GOOGLE -> {
                val hasToken = !value.purchaseToken.isNullOrBlank()
                if (!hasToken) {
                    addViolation(context, "해당 스토어는 purchaseToken이 필수입니다.", "purchaseToken")
                }
                hasToken
            }
            StoreType.APPLE -> true
        }
    }

    private fun addViolation(context: ConstraintValidatorContext, message: String, property: String) {
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(property)
            .addConstraintViolation()
    }
}
