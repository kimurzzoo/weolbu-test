package com.example.weolbutest.db.entity.auth

import com.example.weolbutest.auth.enum.MemberType
import com.example.weolbutest.db.cipher.DataConverter
import jakarta.persistence.*

@Entity
class Member(
	@Column(name = "m_name", nullable = false)
	@Convert(converter = DataConverter::class)
	var name: String = "",

	@Column(nullable = false, unique = true)
	@Convert(converter = DataConverter::class)
	var email: String = "",

	@Column(nullable = false)
	@Convert(converter = DataConverter::class)
	var phoneNumber: String = "",

	@Column(
		name = "m_password",
		nullable = false
	) // 6자 이상 10자 이하, 영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요
	var password: String = "",

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	var memberType: MemberType = MemberType.STUDENT
) {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	var id: Long? = null
}