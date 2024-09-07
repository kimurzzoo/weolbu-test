package com.example.weolbutest.auth.util

import com.example.weolbutest.domain.auth.util.JwtProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class JwtProviderTest {
	@Autowired
	private lateinit var jwtProvider: JwtProvider

	@Test
	fun `JWT 생성 후 인증 테스트`() {
		val email = "kimurzzoo@gmail.com"
		val jwt = jwtProvider.createAccessToken(email)
		val extractedEmail = jwtProvider.getEmail(jwt)
		assertEquals(email, extractedEmail)
	}
}