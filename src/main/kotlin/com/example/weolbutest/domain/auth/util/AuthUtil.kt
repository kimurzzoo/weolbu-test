package com.example.weolbutest.domain.auth.util

import com.example.weolbutest.db.entity.auth.Member
import com.example.weolbutest.domain.auth.model.MemberDetails
import org.springframework.security.core.context.SecurityContextHolder

object AuthUtil {
	fun getMember(): Member {
		val memberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails

		return memberDetails.member
	}
}