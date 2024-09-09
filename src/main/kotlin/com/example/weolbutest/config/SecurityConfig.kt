package com.example.weolbutest.config

import com.example.weolbutest.domain.auth.enm.MemberType
import com.example.weolbutest.filter.auth.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtFilter: JwtFilter) {

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
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
			.authorizeHttpRequests { authRequest ->
				authRequest
					.requestMatchers( // root 누구나 접근가능
						"/swagger/**",
						"/swagger-ui/**",
						"/v3/api-docs/**"
					).permitAll()
					.requestMatchers("/h2-console/**").permitAll()
					.requestMatchers("/member/**").permitAll()
					.requestMatchers("/lecture/teacher/**")
					.hasAuthority("ROLE_${MemberType.TEACHER.name}")
					.anyRequest().authenticated()
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