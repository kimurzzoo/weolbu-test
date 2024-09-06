package com.example.weolbutest.db.cipher

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoder : PasswordEncoder {
	override fun encode(rawPassword: CharSequence?): String {
		return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt())
	}

	override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
		return BCrypt.checkpw(rawPassword.toString(), encodedPassword)
	}
}