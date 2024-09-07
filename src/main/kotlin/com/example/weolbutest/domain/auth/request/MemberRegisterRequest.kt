package com.example.weolbutest.domain.auth.request

import com.example.weolbutest.domain.auth.enum.MemberType
import com.example.weolbutest.db.entity.auth.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class MemberRegisterRequest(
	@NotBlank
	var name: String,

	@Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}\$")
	var email: String,

	@Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
	var phoneNumber: String,

	@Pattern(regexp = "^(?=(.*[a-z].*)|(.*[A-Z].*))(?=(.*[a-z].*)|(.*\\d.*))(?=(.*[A-Z].*)|(.*\\d.*))[A-Za-z\\d]{6,10}\$") // 6자 이상 10자 이하, 영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요
	var password: String,

	@NotBlank
	var memberType: String
) {
	fun toMember(): Member {
		return Member(
			name = name,
			email = email,
			phoneNumber = phoneNumber,
			password = password,
			memberType = MemberType.valueOf(memberType)
		)
	}
}