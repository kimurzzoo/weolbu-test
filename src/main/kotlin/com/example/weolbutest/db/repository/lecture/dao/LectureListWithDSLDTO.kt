package com.example.weolbutest.db.repository.lecture.dao

class LectureListWithDSLDTO(
	var id: Long,
	var teacherName: String,
	var lectureName: String,
	var price: Long,
	var currentStudentCnt: Int,
	var maxStudentCnt: Int,
	var enrollRatio: Double
) {
	constructor() : this(0, "", "", 0, 0, 0, 0.0)

	override fun toString(): String {
		return "LectureListWithDSLDTO(id=${id}, teacherName=${teacherName}, lectureName=${lectureName}, price=${price}, currentStudentCnt=${currentStudentCnt}, maxStudentCnt=${maxStudentCnt}, enrollRatio=${enrollRatio})"
	}
}