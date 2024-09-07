package com.example.weolbutest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

	@Bean
	fun filterChain(
		http: HttpSecurity
	): SecurityFilterChain {

		http
			.csrf { csrf -> csrf.disable() }
			.cors { cors -> cors.configurationSource(corsConfigurationSource()) }
			.formLogin { formLogin -> formLogin.disable() }
			.httpBasic { httpBasic -> httpBasic.disable() }
			.headers { it.frameOptions { option -> option.sameOrigin() } }
			.sessionManagement { sessionManager ->
				sessionManager.sessionCreationPolicy(
					SessionCreationPolicy.ALWAYS
				)
			}
			.authorizeHttpRequests { authRequest ->
				authRequest
					.anyRequest().permitAll()
			}

		return http.build()
	}

	fun corsConfigurationSource(): CorsConfigurationSource {
		val configuration = CorsConfiguration()

		// 로컬 테스트
		configuration.addAllowedOrigin("http://localhost:3000")

		configuration.allowedMethods =
			mutableListOf("GET", "POST", "OPTIONS", "PUT", "DELETE")

		configuration.allowedHeaders = mutableListOf("*")

		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", configuration)
		return source
	}
}