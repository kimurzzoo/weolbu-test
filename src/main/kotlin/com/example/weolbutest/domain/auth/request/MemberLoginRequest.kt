package com.example.weolbutest.domain.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

class MemberLoginRequest(
	@field:Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}\$")
	var email: String,

	@field:Pattern(regexp = "^(?=(.*[a-z].*)|(.*[A-Z].*))(?=(.*[a-z].*)|(.*\\d.*))(?=(.*[A-Z].*)|(.*\\d.*))[A-Za-z\\d]{6,10}\$") // 6자 이상 10자 이하, 영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요
	var password: String
)