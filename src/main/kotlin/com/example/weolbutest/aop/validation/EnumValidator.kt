package com.example.weolbutest.aop.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


class EnumValidator : ConstraintValidator<ValidEnum?, String> {
	private var annotation: ValidEnum? = null

	override fun initialize(constraintAnnotation: ValidEnum?) {
		this.annotation = constraintAnnotation
	}

	override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
		var result = false
		val enumValues = annotation!!.enumClass.java.enumConstants
		if (enumValues != null) {
			for (enumValue in enumValues) {
				if (value == enumValue.name) {
					result = true
					break
				}
			}
		}

		return result
	}
}