package com.example.weolbutest.domain.auth.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider {

	@Value("\${jwt.secret-key}")
	private var secretKeyString: String = ""

	@Value("\${jwt.expiration}")
	private var expiration: Long = 0

	private lateinit var secretKey: SecretKey

	@PostConstruct
	protected fun init() {
		secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray());
	}

	fun createAccessToken(email: String): String {
		val expirationDate = Date(System.currentTimeMillis() + expiration)

		return Jwts.builder()
			.claim("email", email)
			.expiration(expirationDate)
			.signWith(secretKey)
			.compact()
	}

	fun getEmail(jwt: String): String {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).payload["email"] as String
	}
}