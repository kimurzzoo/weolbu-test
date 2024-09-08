package com.example.weolbutest.lecture.service

import com.example.weolbutest.db.repository.lecture.EnrollLectureRepository
import com.example.weolbutest.db.repository.lecture.LectureRepository
import com.example.weolbutest.domain.auth.enm.MemberType
import com.example.weolbutest.domain.auth.model.MemberDetails
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.service.MemberService
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.example.weolbutest.domain.lecture.service.LectureService
import com.example.weolbutest.domain.lecture.value.LectureValue.Companion.lectureListPageLimit
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

	@Autowired
	lateinit var enrollLectureRepository: EnrollLectureRepository

	var memberRegisterRequest = MemberRegisterRequest(
		name = "김영호",
		email = "kimurzzoo@gmail.com",
		phoneNumber = "010-7303-6193",
		password = "Test1234",
		memberType = "TEACHER"
	)

	var memberRegisterRequest1 = MemberRegisterRequest(
		name = "김호영",
		email = "kimurzzoo@naver.com",
		phoneNumber = "010-7303-6193",
		password = "Test1234",
		memberType = "STUDENT"
	)

	var memberRegisterRequest2 = MemberRegisterRequest(
		name = "호영김",
		email = "youngho.kim@gmail.com",
		phoneNumber = "010-7303-6193",
		password = "Test1234",
		memberType = "TEACHER"
	)

	val lectureRegisterRequestList = mutableListOf<LectureRegisterRequest>()

	var lectureRegisterRequest1 = LectureRegisterRequest(
		name = "프로그래밍 강의",
		maxStudentCnt = 10,
		price = 10000
	)

	var lectureRegisterRequest2 = LectureRegisterRequest(
		name = "투자 강의",
		maxStudentCnt = 20,
		price = 20000
	)

	var lectureRegisterRequest3 = LectureRegisterRequest(
		name = "먹방 강의",
		maxStudentCnt = 30,
		price = 30000
	)

	var lectureRegisterRequest4 = LectureRegisterRequest(
		name = "요리 강의",
		maxStudentCnt = 40,
		price = 40000
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

	@Nested
	@DisplayName("수강신청 테스트")
	inner class EnrollLectureTest {

		@BeforeEach
		fun setup() {
			val memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			val member = memberDetails.member

			for (i: Long in 0L..25L) {
				val newLectureRegisterRequest = LectureRegisterRequest(
					name = "프로그래밍 강의${i}",
					maxStudentCnt = 10 * i.toInt(),
					price = 10000 * i
				)
				lectureRegisterRequestList.add(newLectureRegisterRequest)
				lectureService.register(member.id!!, newLectureRegisterRequest)
			}

			memberService.register(memberRegisterRequest1)
			memberService.register(memberRegisterRequest2)
		}

		@Test
		fun `정상적인 경우`() {
			memberService.login(memberRegisterRequest1.email, memberRegisterRequest1.password)

			val memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			val member = memberDetails.member

			val lectures = lectureRepository.findAll()
			val lectureIdList = lectures.map { it.id!! }

			lectureService.enroll(member.id!!, lectureIdList)

			val enrollLectureList = enrollLectureRepository.findByLectureIdInAndStudentId(lectureIdList, member.id!!)

			assertEquals(lectures.size, enrollLectureList.size)
		}
	}

	@Nested
	@DisplayName("강의 리스트 테스트")
	inner class LectureListTest {

		@BeforeEach
		fun setup() {
			memberService.register(memberRegisterRequest1)
			memberService.register(memberRegisterRequest2)

			// 첫번째 회원
			var memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			var member = memberDetails.member

			for (i: Long in 1L..45L) { // 강의 등록
				val newLectureRegisterRequest = LectureRegisterRequest(
					name = "프로그래밍 강의${i}",
					maxStudentCnt = 10 * i.toInt(),
					price = 10000 * i
				)
				lectureRegisterRequestList.add(newLectureRegisterRequest)
				lectureService.register(member.id!!, newLectureRegisterRequest)
			}

			val lectures = lectureRepository.findAll()
			var lectureIdList = lectures.map { it.id!! }

			lectureService.enroll(member.id!!, lectureIdList) // 전부 수강신청

			// 두번째 회원
			memberService.login(memberRegisterRequest1.email, memberRegisterRequest1.password)

			memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			member = memberDetails.member

			lectureIdList = lectures.subList(lectures.size / 2, lectures.size).map { it.id!! } // 뒤 절반만

			lectureService.enroll(member.id!!, lectureIdList)

			// 세번째 회원
			memberService.login(memberRegisterRequest2.email, memberRegisterRequest2.password)

			memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
			member = memberDetails.member

			lectureIdList =
				lectures.subList(lectures.size * 2 / 3 - 1, lectures.size * 2 / 3).map { it.id!! } // 2/3 지점 강의만

			lectureService.enroll(member.id!!, lectureIdList)
		}

		@Test
		fun `최신 순 정렬`() {
			val lectureList = lectureService.lectureList(0, LectureListOrderType.RECENT_REGISTERED)
			println(lectureList)
			assertEquals(lectureListPageLimit, lectureList.size)
			assertEquals(2, lectureList[0].currentStudentCnt)
		}

		@Test
		fun `신청자 많은 순 정렬`() {
			val lectureList = lectureService.lectureList(0, LectureListOrderType.ENROLLED_COUNT)
			println(lectureList)
			assertEquals(lectureListPageLimit, lectureList.size)
			assertEquals(3, lectureList[0].currentStudentCnt)
		}

		@Test
		fun `신청률 높은 순 정렬`() {
			val lectureList = lectureService.lectureList(0, LectureListOrderType.ENROLLED_RATIO)
			println(lectureList)
			assertEquals(lectureListPageLimit, lectureList.size)
			assertEquals(0.1, lectureList[0].enrollRatio)
		}
	}
}