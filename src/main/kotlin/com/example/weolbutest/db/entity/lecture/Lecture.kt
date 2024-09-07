package com.example.weolbutest.db.entity.lecture

import com.example.weolbutest.db.entity.common.DateEntity
import jakarta.persistence.*

@Entity
class Lecture(
	@Column(name = "teacher_id", nullable = false)
	var teacherId: Long,

	@Column(nullable = false)
	var name: String,

	@Column(nullable = false)
	var maxStudentCnt: Int,

	@Column(nullable = false)
	var price: Long
) : DateEntity() {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	var id: Long? = null

	@Column(nullable = false)
	var isDeleted: Boolean = false
}