package com.example.weolbutest.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
	info = io.swagger.v3.oas.annotations.info.Info(
		title = "B2C API 명세서",
		description = "B2C API 명세서 v1",
		version = "v1"
	)
)
@Configuration
class SwaggerConfig {

	fun buildSecurityOpenApi(): OpenApiCustomizer {
		val securityScheme = SecurityScheme()
			.name("Authorization")
			.type(SecurityScheme.Type.APIKEY)
			.`in`(SecurityScheme.In.HEADER)

		return OpenApiCustomizer { OpenApi: OpenAPI ->
			OpenApi
				.addSecurityItem(SecurityRequirement().addList("jwt"))
				.components.addSecuritySchemes("jwt", securityScheme)
		}
	}

	@Bean
	fun memberApi(): GroupedOpenApi {
		val paths = arrayOf("/member/**")

		return GroupedOpenApi.builder()
			.group("member")
			.pathsToMatch(*paths)
			.build()
	}

	@Bean
	fun lectureApi(): GroupedOpenApi {
		val paths = arrayOf("/lecture/**")

		return GroupedOpenApi.builder()
			.group("lecture")
			.pathsToMatch(*paths)
			.addOpenApiCustomizer(buildSecurityOpenApi())
			.build()
	}
}