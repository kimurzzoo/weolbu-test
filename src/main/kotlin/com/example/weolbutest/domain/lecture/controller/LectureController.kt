package com.example.weolbutest.domain.lecture.controller

import com.example.weolbutest.domain.auth.util.AuthUtil
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.example.weolbutest.domain.lecture.service.LectureService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lecture")
class LectureController(private val lectureService: LectureService) {
	@PostMapping("/teacher/register")
	fun register(@RequestBody @Valid req: LectureRegisterRequest) {
		val member = AuthUtil.getMember()

		lectureService.register(member.id!!, req)
	}
}