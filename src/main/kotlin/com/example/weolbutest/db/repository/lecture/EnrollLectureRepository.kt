package com.example.weolbutest.db.repository.lecture

import com.example.weolbutest.db.entity.lecture.EnrollLecture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnrollLectureRepository : JpaRepository<EnrollLecture, Long> {
	fun findByLectureIdInAndStudentId(
		lectureIdList: List<Long>,
		studentId: Long
	): List<EnrollLecture>

	fun findByLectureId(lectureId: Long): List<EnrollLecture>
}