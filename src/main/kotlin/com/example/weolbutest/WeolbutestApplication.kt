package com.example.weolbutest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class WeolbutestApplication

fun main(args: Array<String>) {
	runApplication<WeolbutestApplication>(*args)
}
