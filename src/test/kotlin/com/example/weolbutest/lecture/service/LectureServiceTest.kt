package com.example.weolbutest.lecture.service

import com.example.weolbutest.db.repository.lecture.LectureRepository
import com.example.weolbutest.domain.auth.enum.MemberType
import com.example.weolbutest.domain.auth.model.MemberDetails
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.service.MemberService
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.example.weolbutest.domain.lecture.service.LectureService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
@DisplayName("강의 관련 서비스 테스트")
class LectureServiceTest {

	@Autowired
	lateinit var memberService: MemberService

	@Autowired
	lateinit var lectureService: LectureService

	@Autowired
	lateinit var lectureRepository: LectureRepository

	var memberRegisterRequest = MemberRegisterRequest(
		name = "김영호",
		email = "kimurzzoo@gmail.com",
		phoneNumber = "010-7303-6193",
		password = "Test1234",
		memberType = "TEACHER"
	)

	@BeforeEach
	fun setup() {
		memberService.register(memberRegisterRequest)
		memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)
	}

	@Nested
	@DisplayName("강의 등록 테스트")
	inner class RegisterTest {

		var lectureRegisterRequest = LectureRegisterRequest(
			name = "프로그래밍 강의",
			maxStudentCnt = 10,
			price = 10000
		)

		@Test
		fun `정상적인 경우`() {
			val memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			val member = memberDetails.member
			assertEquals(MemberType.TEACHER, member.memberType)

			lectureService.register(member.id!!, lectureRegisterRequest)

			val lecture = lectureRepository.findByTeacherIdAndName(member.id!!, lectureRegisterRequest.name)
				.orElseThrow { Exception() }
			assertEquals(lectureRegisterRequest.name, lecture.name)
			assertEquals(lectureRegisterRequest.maxStudentCnt, lecture.maxStudentCnt)
			assertEquals(lectureRegisterRequest.price, lecture.price)
		}
	}
}