package com.example.weolbutest.domain.auth.service

import com.example.weolbutest.db.cipher.BCryptPasswordEncoder
import com.example.weolbutest.db.repository.auth.MemberRepository
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.response.MemberLoginResponse
import com.example.weolbutest.domain.auth.util.JwtProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
	private val jwtProvider: JwtProvider,
	private val memberRepository: MemberRepository,
	private val bCryptPasswordEncoder: BCryptPasswordEncoder,
	private val authenticationManagerBuilder: AuthenticationManagerBuilder
) {

	@Transactional
	fun register(req: MemberRegisterRequest) {
		val member = req.toMember()
		member.password = bCryptPasswordEncoder.encode(member.password)
		memberRepository.save(member)
	}

	fun login(email: String, password: String): MemberLoginResponse {
		val authenticationToken = UsernamePasswordAuthenticationToken(email, password)

		val authentication: Authentication = authenticationManagerBuilder.getObject()
			.authenticate(authenticationToken)
		SecurityContextHolder.getContext().authentication = authentication

		val jwt = jwtProvider.createAccessToken(email)

		return MemberLoginResponse(accessToken = jwt)
	}
}