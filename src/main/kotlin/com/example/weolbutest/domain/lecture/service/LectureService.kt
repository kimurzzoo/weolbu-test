package com.example.weolbutest.domain.lecture.service

import com.example.weolbutest.db.entity.lecture.EnrollLecture
import com.example.weolbutest.db.repository.lecture.EnrollLectureRepository
import com.example.weolbutest.db.repository.lecture.LectureRepository
import com.example.weolbutest.db.repository.lecture.dao.LectureListWithDSLDTO
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import com.example.weolbutest.domain.lecture.response.LectureEnrollResponse
import com.example.weolbutest.domain.lecture.value.LectureValue.Companion.lectureListPageLimit
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional


@Service
class LectureService(
	private val lectureRepository: LectureRepository,
	private val enrollLectureRepository: EnrollLectureRepository,
	private val redisTemplate: RedisTemplate<String, Int>
) {
	val valueOperations: ValueOperations<String, Int> = redisTemplate.opsForValue()

	@Transactional(rollbackFor = [Exception::class])
	fun register(teacherId: Long, req: LectureRegisterRequest) {
		val lecture = lectureRepository.save(req.toLecture(teacherId))
		valueOperations.set("lecture:${lecture.id!!}", lecture.maxStudentCnt)
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
	fun enroll(studentId: Long, lectureList: List<Long>): LectureEnrollResponse {


		val lectureEntityList = lectureRepository.findByIdInAndIsDeletedFalse(lectureList)
		if (lectureEntityList.size != lectureList.size) {
			throw IllegalArgumentException("Invalid Lecture")
		}

		val enrollList = mutableListOf<EnrollLecture>()
		val notEnrolledList = mutableListOf<String>()

		lectureEntityList.forEach {
			try {
				valueOperations.decrement("lecture:${it.id!!}")

				enrollList.add(
					EnrollLecture(
						it.id!!,
						studentId
					)
				)
			} catch (e: Exception) {
				notEnrolledList.add(it.name)
			}
		}

		enrollLectureRepository.saveAll(enrollList)
		return LectureEnrollResponse(
			notEnrolled = notEnrolledList
		)
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, noRollbackFor = [Exception::class])
	fun lectureList(page: Int, lectureListOrderType: LectureListOrderType): List<LectureListWithDSLDTO> {
		val pageable = PageRequest.of(page, lectureListPageLimit)
		return lectureRepository.lectureListWithDSL(lectureListOrderType, pageable)
	}
}