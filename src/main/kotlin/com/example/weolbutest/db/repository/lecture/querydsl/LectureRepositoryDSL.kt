package com.example.weolbutest.db.repository.lecture.querydsl

import com.example.weolbutest.db.repository.lecture.dao.LectureListWithDSLDTO
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType
import org.springframework.data.domain.Pageable

interface LectureRepositoryDSL {
	fun lectureListWithDSL(orderType: LectureListOrderType, pageable: Pageable): List<LectureListWithDSLDTO>
}