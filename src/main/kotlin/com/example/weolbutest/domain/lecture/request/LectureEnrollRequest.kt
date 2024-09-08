package com.example.weolbutest.domain.lecture.request

import jakarta.validation.constraints.NotEmpty

class LectureEnrollRequest(
	@NotEmpty
	var lectureList: List<Long>
)