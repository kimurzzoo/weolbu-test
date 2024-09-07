package com.example.weolbutest.domain.auth.service

import com.example.weolbutest.domain.auth.model.MemberDetails
import com.example.weolbutest.db.repository.auth.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

@Service
class MemberDetailsService(private val memberRepository: MemberRepository) : UserDetailsService {

	@Transactional(readOnly = true, noRollbackFor = [Exception::class])
	override fun loadUserByUsername(username: String?): UserDetails {
		if (!StringUtils.hasText(username)) {
			throw UsernameNotFoundException("Username is null")
		}

		val member = memberRepository.findByEmail(username!!).orElseThrow {
			UsernameNotFoundException(
				"User does not exist"
			)
		}

		return MemberDetails(member)
	}
}