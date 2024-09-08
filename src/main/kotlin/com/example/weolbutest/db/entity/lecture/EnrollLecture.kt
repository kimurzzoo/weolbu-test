package com.example.weolbutest.db.entity.lecture

import com.example.weolbutest.db.entity.common.DateEntity
import jakarta.persistence.*

@Entity
class EnrollLecture(
	@Column(nullable = false)
	var lectureId: Long,
	@Column(name = "student_id", nullable = false)
	var studentId: Long,
) : DateEntity() {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	var id: Long? = null
}