package com.example.weolbutest.domain.lecture.request

import com.example.weolbutest.db.entity.lecture.Lecture
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

class LectureRegisterRequest(
	@field:NotBlank
	var name: String,

	@field:Positive
	var maxStudentCnt: Int,

	@field:PositiveOrZero
	var price: Long
) {
	fun toLecture(teacherId: Long): Lecture {
		return Lecture(
			teacherId = teacherId,
			name = name,
			maxStudentCnt = maxStudentCnt,
			price = price
		)
	}
}