package com.example.weolbutest.filter.auth

import com.example.weolbutest.domain.auth.service.MemberDetailsService
import com.example.weolbutest.domain.auth.util.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class JwtFilter(
	private val jwtProvider: JwtProvider,
	private val memberDetailsService: MemberDetailsService
) : GenericFilterBean() {
	override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
		try {
			val jwt = (request as HttpServletRequest).getHeader("access-token")
			val email = jwtProvider.getEmail(jwt)
			val memberDetails = memberDetailsService.loadUserByUsername(email)

			val authentication = UsernamePasswordAuthenticationToken(memberDetails, "", memberDetails.authorities)
			SecurityContextHolder.getContext().authentication = authentication
		} catch (_: Exception) {

		} finally {
			chain?.doFilter(request, response)
		}
	}
}