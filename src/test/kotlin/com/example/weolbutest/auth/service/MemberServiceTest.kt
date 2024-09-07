package com.example.weolbutest.auth.service

import com.example.weolbutest.db.repository.auth.MemberRepository
import com.example.weolbutest.domain.auth.enum.MemberType
import com.example.weolbutest.domain.auth.model.MemberDetails
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.service.MemberService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
@DisplayName("회원 관련 서비스 테스트")
class MemberServiceTest {

	@Autowired
	lateinit var memberService: MemberService

	@Autowired
	lateinit var memberRepository: MemberRepository

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
			memberService.register(memberRegisterRequest)

			val member = memberRepository.findByEmail(memberRegisterRequest.email).orElseThrow { Exception() }

			assertEquals(memberRegisterRequest.name, member.name)
			assertEquals(memberRegisterRequest.email, member.email)
			assertEquals(MemberType.valueOf(memberRegisterRequest.memberType), member.memberType)
		}

		@Test
		fun `회원 구분이 형식에 맞지 않은 경우`() {
			memberRegisterRequest.memberType = "ETC"

			assertThrows<IllegalArgumentException> {
				memberService.register(memberRegisterRequest)
			}
		}

		@Test
		fun `이미 가입된 회원인 경우`() {
			memberService.register(memberRegisterRequest)

			assertThrows<DataIntegrityViolationException> {
				memberService.register(memberRegisterRequest)
			}
		}
	}


	@Nested
	@DisplayName("로그인 테스트")
	inner class LoginTest {
		@Test
		fun `정상적인 경우`() {
			memberService.register(memberRegisterRequest)
			memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)

			val memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			val member = memberDetails.member
			assertEquals(memberRegisterRequest.email, member.email)
			assertEquals(MemberType.valueOf(memberRegisterRequest.memberType), member.memberType)
		}

		@Test
		fun `비밀번호가 틀린 경우`() {
			memberService.register(memberRegisterRequest)
			assertThrows<BadCredentialsException> {
				memberService.login(memberRegisterRequest.email, "123")
			}
		}

		@Test
		fun `이메일과 일치하는 회원이 없는 경우`() {
			memberService.register(memberRegisterRequest)
			assertThrows<BadCredentialsException> {
				memberService.login("youngho.kim@gmail.com", memberRegisterRequest.password)
			}
		}
	}
}