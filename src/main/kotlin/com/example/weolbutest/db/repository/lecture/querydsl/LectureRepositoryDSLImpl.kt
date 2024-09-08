package com.example.weolbutest.db.repository.lecture.querydsl

import com.example.weolbutest.db.entity.auth.QMember.member
import com.example.weolbutest.db.entity.lecture.QEnrollLecture.enrollLecture
import com.example.weolbutest.db.entity.lecture.QLecture.lecture
import com.example.weolbutest.db.repository.lecture.dao.LectureListWithDSLDTO
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType
import com.example.weolbutest.domain.lecture.enm.LectureListOrderType.*
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable

class LectureRepositoryDSLImpl(private val jpaQueryFactory: JPAQueryFactory) : LectureRepositoryDSL {
	override fun lectureListWithDSL(orderType: LectureListOrderType, pageable: Pageable): List<LectureListWithDSLDTO> {
		val orderSpecifier = when (orderType) {
			RECENT_REGISTERED -> OrderSpecifier(Order.DESC, lecture.createTimestamp)
			ENROLLED_COUNT -> OrderSpecifier(Order.DESC, enrollLecture.id.count())
			ENROLLED_RATIO -> OrderSpecifier(
				Order.DESC,
				enrollLecture.id.count().doubleValue().divide(lecture.maxStudentCnt.doubleValue())
			)
		}

		return jpaQueryFactory.select(
			Projections.bean(
				LectureListWithDSLDTO::class.java,
				lecture.id,
				member.name.`as`("teacherName"),
				lecture.name.`as`("lectureName"),
				lecture.price,
				enrollLecture.id.count().intValue().`as`("currentStudentCnt"),
				lecture.maxStudentCnt,
				enrollLecture.id.count().doubleValue().divide(lecture.maxStudentCnt.doubleValue()).`as`("enrollRatio")
			)
		)
			.from(lecture)
			.innerJoin(member)
			.on(lecture.teacherId.eq(member.id))
			.leftJoin(enrollLecture)
			.on(lecture.id.eq(enrollLecture.lectureId))
			.where(lecture.isDeleted.isFalse)
			.groupBy(lecture.id, lecture.maxStudentCnt)
			.orderBy(orderSpecifier)
			.limit(pageable.pageSize.toLong())
			.offset(pageable.offset)
			.fetch()
	}
}