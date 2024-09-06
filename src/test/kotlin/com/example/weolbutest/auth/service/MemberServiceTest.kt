package com.example.weolbutest.auth.service

import com.example.weolbutest.auth.request.MemberRegisterRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	lateinit var memberService: MemberService

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

	@Test
	fun `회원 등록 시 정상적인 경우`() {
		memberService.register(memberRegisterRequest)
	}

	@Test
	fun `회원 등록 시 회원 구분이 형식에 맞지 않은 경우`() {
		memberRegisterRequest.memberType = "ETC"

		assertThrows<IllegalArgumentException> {
			memberService.register(memberRegisterRequest)
		}
	}

	@Test
	fun `회원 등록 시 이미 가입된 회원인 경우`() {
		memberService.register(memberRegisterRequest)

		assertThrows<DataIntegrityViolationException> {
			memberService.register(memberRegisterRequest)
		}
	}

	@Test
	fun `로그인 시 정상적인 경우`() {
		memberService.register(memberRegisterRequest)
		memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)
	}

	@Test
	fun `로그인 시 비밀번호가 틀린 경우`() {
		memberService.register(memberRegisterRequest)
		memberService.login(memberRegisterRequest.email, "123")
	}

	@Test
	fun `로그인 시 이메일과 일치하는 회원이 없는 경우`() {
		memberService.register(memberRegisterRequest)
		memberService.login("youngho.kim@gmail.com", memberRegisterRequest.password)
	}
}