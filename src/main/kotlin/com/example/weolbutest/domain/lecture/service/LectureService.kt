package com.example.weolbutest.domain.lecture.service

import com.example.weolbutest.db.repository.lecture.LectureRepository
import com.example.weolbutest.domain.lecture.request.LectureRegisterRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LectureService(private val lectureRepository: LectureRepository) {

	@Transactional(rollbackFor = [Exception::class])
	fun register(teacherId: Long, req: LectureRegisterRequest) {
		lectureRepository.save(req.toLecture(teacherId))
	}
}