package com.example.weolbutest.domain.lecture.request

import com.example.weolbutest.aop.validation.ValidEnum
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType
import jakarta.validation.constraints.Positive

class LectureListRequest(
	@Positive
	var page: Int,

	@ValidEnum(enumClass = LectureListOrderType::class)
	var lectureListOrderType: String
)