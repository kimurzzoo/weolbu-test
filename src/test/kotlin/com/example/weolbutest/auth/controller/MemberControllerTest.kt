package com.example.weolbutest.auth.controller

import com.example.weolbutest.domain.auth.request.MemberLoginRequest
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.service.MemberService
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
@DisplayName("회원 관련 통합 테스트")
class MemberControllerTest {
	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var memberService: MemberService

	var memberRegisterRequest = MemberRegisterRequest(
		name = "김영호",
		email = "kimurzzoo@gmail.com",
		phoneNumber = "010-7303-6193",
		password = "Test1234",
		memberType = "STUDENT"
	)

	@BeforeEach
	fun setup() {
		memberRegisterRequest = MemberRegisterRequest(
			name = "김영호",
			email = "kimurzzoo@gmail.com",
			phoneNumber = "010-7303-6193",
			password = "Test1234",
			memberType = "STUDENT"
		)
	}

	@Nested
	@DisplayName("회원 등록 테스트")
	inner class RegisterTest {
		@Test
		fun `정상적인 경우`() {
			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberRegisterRequest)

			val uri = "/member/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().isOk)
		}

		@Test
		fun `이메일이 형식에 맞지 않는 경우`() {
			memberRegisterRequest.email = "aoigjwpgia"

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberRegisterRequest)

			val uri = "/member/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}

		@Test
		fun `비밀번호가 형식에 맞지 않는 경우`() {
			memberRegisterRequest.password = "111"

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberRegisterRequest)

			val uri = "/member/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}

		@Test
		fun `전화번호가 형식에 맞지 않는 경우`() {
			memberRegisterRequest.phoneNumber = "0101010-19"

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberRegisterRequest)

			val uri = "/member/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}

		@Test
		fun `회원 구분이 형식에 맞지 않는 경우`() {
			memberRegisterRequest.memberType = "ETC"

			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberRegisterRequest)

			val uri = "/member/register"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}
	}

	@Nested
	@DisplayName("로그인 테스트")
	inner class LoginTest {
		var memberLoginRequest = MemberLoginRequest(
			email = memberRegisterRequest.email,
			password = memberRegisterRequest.password
		)

		@BeforeEach
		fun setup() {
			memberService.register(memberRegisterRequest)
			memberLoginRequest = MemberLoginRequest(
				email = memberRegisterRequest.email,
				password = memberRegisterRequest.password
			)
		}

		@Test
		fun `정상적인 경우`() {
			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberLoginRequest)

			val uri = "/member/login"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().isOk)
				.andExpect(MockMvcResultMatchers.jsonPath("accessToken").isString)
		}

		@Test
		fun `이메일이 형식에 맞지 않는 경우`() {
			memberLoginRequest.email = "awerohi"
			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberLoginRequest)

			val uri = "/member/login"

			mockMvc.perform(
				MockMvcRequestBuilders.post(uri)
					.content(json)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError)
		}

		@Test
		fun `비밀번호가 형식에 맞지 않는 경우`() {
			memberLoginRequest.password = "1111"
			// JSON 형식의 문자열로 변환
			val json = ObjectMapper().writeValueAsString(memberLoginRequest)

			val uri = "/member/login"

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