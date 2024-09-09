package com.example.weolbutest.lecture.service

import com.example.weolbutest.db.repository.auth.MemberRepository
import com.example.weolbutest.db.repository.lecture.EnrollLectureRepository
import com.example.weolbutest.db.repository.lecture.LectureRepository
import com.example.weolbutest.domain.auth.model.MemberDetails
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.service.MemberService
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.example.weolbutest.domain.lecture.service.LectureService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.assertEquals

@SpringBootTest
@DisplayName("강의 관련 서비스 동시성 제어 테스트")
class LectureConcurrentTest {
	@Autowired
	lateinit var memberService: MemberService

	@Autowired
	lateinit var lectureService: LectureService

	@Autowired
	lateinit var memberRepository: MemberRepository

	@Autowired
	lateinit var lectureRepository: LectureRepository

	@Autowired
	lateinit var enrollLectureRepository: EnrollLectureRepository

	@Autowired
	lateinit var redisTemplate: RedisTemplate<String, Any>

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

	@BeforeEach
	fun setup() {
		memberService.register(memberRegisterRequest)
		memberService.login(memberRegisterRequest.email, memberRegisterRequest.password)

		val memberDetails =
			SecurityContextHolder.getContext().authentication.principal as MemberDetails
		val member = memberDetails.member

		for (i: Long in 1L..25L) {
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

	@AfterEach
	fun finally() {
		memberRepository.deleteAll()
		lectureRepository.deleteAll()
		enrollLectureRepository.deleteAll()
		redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
	}

	@Test
	fun `최대 인원까지 수강신청하지 않는 경우`() {

		val members = memberRepository.findAll()
		val lectures = lectureRepository.findAll()

		val lectureIdList = lectures.subList(0, 2).map { it.id!! }

		val numberOfThreads = 2
		val service = Executors.newFixedThreadPool(numberOfThreads)
		val latch = CountDownLatch(numberOfThreads)

		service.execute {
			try {
				lectureService.enroll(members[0].id!!, lectureIdList)
			} catch (e: Exception) {

			} finally {
				latch.countDown()
			}
		}
		service.execute {
			try {
				lectureService.enroll(members[1].id!!, lectureIdList)
			} catch (e: Exception) {

			} finally {
				latch.countDown()
			}
		}
		latch.await()

		val valueOperations = redisTemplate.opsForValue()
		lectureIdList.forEach {
			val lecture = lectureRepository.findById(it).orElseThrow { Exception() }
			val enrollList = enrollLectureRepository.findByLectureId(it)
			val restCnt = (valueOperations.get("lecture:$it") as String).toInt()
			assertTrue(enrollList.isNotEmpty())
			assertEquals(lecture.maxStudentCnt, enrollList.size + restCnt)
		}
	}

	@Test
	fun `최대 인원 넘겨서 수강신청하는 경우`() {
		for (i: Long in 1L..25L) {
			val newMemberRegisterRequest = MemberRegisterRequest(
				name = "김영호",
				email = "kimurzzoo${i}@gmail.com",
				phoneNumber = "010-7303-6193",
				password = "Test1234",
				memberType = "STUDENT"
			)
			memberService.register(newMemberRegisterRequest)
		}

		val members = memberRepository.findAll()
		val lectures = lectureRepository.findAll()

		val numberOfThreads = 15
		val lectureIdList = lectures.subList(0, numberOfThreads).map { it.id!! }

		val service = Executors.newFixedThreadPool(numberOfThreads)
		val latch = CountDownLatch(numberOfThreads)

		for (i: Int in 1..numberOfThreads) {
			service.execute {
				try {
					lectureService.enroll(members[i].id!!, lectureIdList)
				} catch (e: Exception) {

				} finally {
					latch.countDown()
				}
			}
		}
		latch.await()

		val valueOperations = redisTemplate.opsForValue()
		lectureIdList.forEach {
			val lecture = lectureRepository.findById(it).orElseThrow { Exception() }
			val enrollList = enrollLectureRepository.findByLectureId(it)
			val restCnt = (valueOperations.get("lecture:$it") as String).toInt()
			assertTrue(enrollList.isNotEmpty())
			assertFalse(restCnt < 0)
			assertEquals(lecture.maxStudentCnt, enrollList.size + restCnt)
		}
	}
}