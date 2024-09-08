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
import java.sql.SQLException


@Service
class LectureService(
	private val lectureRepository: LectureRepository,
	private val enrollLectureRepository: EnrollLectureRepository,
	private val redisTemplate: RedisTemplate<String, Any>
) {
	val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

	@Transactional(rollbackFor = [Exception::class])
	fun register(teacherId: Long, req: LectureRegisterRequest) {
		val lecture = lectureRepository.save(req.toLecture(teacherId))
		valueOperations.set("lecture:${lecture.id!!}", lecture.maxStudentCnt.toString())
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
	fun enroll(studentId: Long, lectureList: List<Long>): LectureEnrollResponse {
		val enrollList = mutableListOf<EnrollLecture>()
		val notEnrolledList = mutableListOf<String>()

		try {
			val lectureEntityList = lectureRepository.findByIdInAndIsDeletedFalse(lectureList)
			if (lectureEntityList.size != lectureList.size) {
				throw IllegalArgumentException("Invalid Lecture")
			}

			lectureEntityList.forEach {
				val currentCnt = valueOperations.decrement("lecture:${it.id!!}")
				if (currentCnt == null || currentCnt < 0) {
					valueOperations.increment("lecture:${it.id!!}")
					notEnrolledList.add(it.name)
				} else {
					enrollList.add(
						EnrollLecture(
							it.id!!,
							studentId
						)
					)
				}
			}

			enrollLectureRepository.saveAll(enrollList)
			return LectureEnrollResponse(
				notEnrolled = notEnrolledList
			)
		} catch (e: Exception) {
			enrollList.forEach {
				valueOperations.increment("lecture:${it.id!!}")
			}
			throw SQLException()
		}

	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, noRollbackFor = [Exception::class])
	fun lectureList(page: Int, lectureListOrderType: LectureListOrderType): List<LectureListWithDSLDTO> {
		val pageable = PageRequest.of(page, lectureListPageLimit)
		return lectureRepository.lectureListWithDSL(lectureListOrderType, pageable)
	}
}