package com.example.weolbutest.auth.service

import com.example.weolbutest.auth.request.MemberRegisterRequest
import com.example.weolbutest.db.cipher.BCryptPasswordEncoder
import com.example.weolbutest.db.repository.MemberRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
	private val memberRepository: MemberRepository,
	private val bCryptPasswordEncoder: BCryptPasswordEncoder,
	private val authenticationManagerBuilder: AuthenticationManagerBuilder
) {

	@Transactional
	fun register(memberRegisterRequest: MemberRegisterRequest) {
		val member = memberRegisterRequest.toMember()
		member.password = bCryptPasswordEncoder.encode(member.password)
		memberRepository.save(member)
	}

	fun login(email: String, password: String) {
		val authenticationToken = UsernamePasswordAuthenticationToken(email, password)

		val authentication: Authentication = authenticationManagerBuilder.getObject()
			.authenticate(authenticationToken)
		SecurityContextHolder.getContext().authentication = authentication
	}
}