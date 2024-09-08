package com.example.weolbutest.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@EnableCaching
@Configuration
class RedisConfig {
	@Value("\${spring.data.redis.host}")
	private val host: String? = null

	@Value("\${spring.data.redis.port}")
	private val port: String? = null

	@Bean
	fun redisConnectionFactory(): RedisConnectionFactory {
		val redisStandaloneConfiguration = RedisStandaloneConfiguration()
		redisStandaloneConfiguration.hostName = host!!
		redisStandaloneConfiguration.port = port!!.toInt()
		val lettuceConnectionFactory = LettuceConnectionFactory(redisStandaloneConfiguration)
		return lettuceConnectionFactory
	}

	@Bean
	fun redisTemplate(): RedisTemplate<String, Any> {
		val redisTemplate = RedisTemplate<String, Any>()
		redisTemplate.connectionFactory = redisConnectionFactory()
		redisTemplate.keySerializer = StringRedisSerializer()
		redisTemplate.valueSerializer = StringRedisSerializer()
		return redisTemplate
	}
}