package com.example.weolbutest.domain.auth.controller

import com.example.weolbutest.domain.auth.request.MemberLoginRequest
import com.example.weolbutest.domain.auth.request.MemberRegisterRequest
import com.example.weolbutest.domain.auth.response.MemberLoginResponse
import com.example.weolbutest.domain.auth.service.MemberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(private val memberService: MemberService) {
	@PostMapping("/register")
	fun register(@RequestBody @Valid req: MemberRegisterRequest) {
		memberService.register(req)
	}

	@PostMapping("/login")
	fun login(@RequestBody @Valid req: MemberLoginRequest): MemberLoginResponse {
		return memberService.login(req.email, req.password)
	}
}