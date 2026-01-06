package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.domain.diary.domain.DiaryInputType
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class DiaryRequestValidator : ConstraintValidator<ValidDiaryRequest, CreateDiaryRequest> {

    override fun isValid(request: CreateDiaryRequest, context: ConstraintValidatorContext): Boolean {
        return when (request.type) {
            DiaryInputType.TEXT -> {
                if (request.text.isNullOrBlank()) {
                    addViolation(context, "TEXT 타입은 일기 내용(text)이 필수입니다.", "text")
                    false
                } else true
            }
            DiaryInputType.PHOTO -> {
                if (request.photoUrl.isNullOrBlank()) {
                    addViolation(context, "PHOTO 타입은 사진 URL(photoUrl)이 필수입니다.", "photoUrl")
                    false
                } else true
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
