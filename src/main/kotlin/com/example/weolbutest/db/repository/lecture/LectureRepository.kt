package com.example.weolbutest.db.repository.lecture

import com.example.weolbutest.db.entity.lecture.Lecture
import com.example.weolbutest.db.repository.lecture.querydsl.LectureRepositoryDSL
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LectureRepository : JpaRepository<Lecture, Long>, LectureRepositoryDSL {
	fun findByTeacherIdAndName(teacherId: Long, name: String): Optional<Lecture>

	fun findByIdInAndIsDeletedFalse(idList: List<Long>): List<Lecture>
}