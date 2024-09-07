package com.example.weolbutest.lecture.controller

import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.service.MemberService
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("강의 관련 통합 테스트")
class LectureControllerTest {
	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var memberService: MemberService

	var memberRegisterRequest = MemberRegisterRequest(
		name = "김영호",
		email = "kimurzzoo@gmail.com",
		phoneNumber = "010-7303-6193",
		password = "Test1234",
		memberType = "TEACHER"
	)

	var lectureRegisterRequest = LectureRegisterRequest(
		name = "프로그래밍 강의",
		maxStudentCnt = 10,
		price = 10000
	)

	var jwt = ""

	@BeforeEach
	fun setup() {
		memberRegisterRequest = MemberRegisterRequest(
			name = "김영호",
			email = "kimurzzoo@gmail.com",
			phoneNumber = "010-7303-6193",
			password = "Test1234",
			memberType = "TEACHER"
		)
		memberService.register(memberRegisterRequest)

		lectureRegisterRequest = LectureRegisterRequest(
			name = "프로그래밍 강의",
			maxStudentCnt = 10,
			price = 10000
		)
	}

	@Nested
	@DisplayName("강의 등록 테스트")
	inner class RegisterTest {
		@Test
		fun `정상적인 경우`() {
			val loginResponse = memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)
			jwt = loginResponse.accessToken

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(lectureRegisterRequest)

			val uri = "/lecture/teacher/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.header("access-token", jwt)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().isOk)
		}

		@Test
		fun `회원이 강사가 아닌 경우`() {
			memberRegisterRequest = MemberRegisterRequest(
				name = "김영호2",
				email = "kimurzzoo@naver.com",
				phoneNumber = "010-7303-6193",
				password = "Test1234",
				memberType = "STUDENT"
			)
			memberService.register(memberRegisterRequest)

			val loginResponse = memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)
			jwt = loginResponse.accessToken

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(lectureRegisterRequest)

			val uri = "/lecture/teacher/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}

		@Test
		fun `최대 수강인원이 비정상적인 경우`() {
			val loginResponse = memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)
			jwt = loginResponse.accessToken

			lectureRegisterRequest.maxStudentCnt = -1

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(lectureRegisterRequest)

			val uri = "/lecture/teacher/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.header("access-token", jwt)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}

		@Test
		fun `가격이 비정상적인 경우`() {
			lectureRegisterRequest.price = -1

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(lectureRegisterRequest)

			val uri = "/lecture/teacher/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}
	}
}