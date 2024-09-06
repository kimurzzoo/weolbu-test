package com.example.weolbutest.auth.model

import com.example.weolbutest.db.entity.auth.Member
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User

class MemberDetails(member: Member) :
	User(
		member.email,
		member.password,
		AuthorityUtils.createAuthorityList("ROLE_${member.memberType.name}")
	) {
}