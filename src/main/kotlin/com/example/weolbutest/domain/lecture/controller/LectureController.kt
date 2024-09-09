package com.example.weolbutest.domain.lecture.controller

import com.example.weolbutest.db.repository.lecture.dao.LectureListWithDSLDTO
import com.example.weolbutest.domain.auth.util.AuthUtil
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType
import com.example.weolbutest.domain.lecture.request.LectureEnrollRequest
import com.example.weolbutest.domain.lecture.request.LectureListRequest
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.example.weolbutest.domain.lecture.response.LectureEnrollResponse
import com.example.weolbutest.domain.lecture.service.LectureService
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lecture")
class LectureController(private val lectureService: LectureService) {
	@PostMapping("/teacher/register")
	fun register(@RequestBody @Valid req: LectureRegisterRequest) {
		val member = AuthUtil.getMember()

		lectureService.register(member.id!!, req)
	}

	@GetMapping("/list")
	fun lectureList(
		@ParameterObject @Valid req: LectureListRequest
	): List<LectureListWithDSLDTO> {
		return lectureService.lectureList(req.page, LectureListOrderType.valueOf(req.lectureListOrderType))
	}

	@PostMapping("/enroll")
	fun enroll(@RequestBody @Valid req: LectureEnrollRequest): LectureEnrollResponse {
		val member = AuthUtil.getMember()

		return lectureService.enroll(member.id!!, req.lectureList)
	}
}