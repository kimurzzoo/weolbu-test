package com.example.weolbutest.db.entity.common

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
abstract class DateEntity {
	@CreatedDate
	@Column(nullable = false, updatable = false)
	var createTimestamp: Long = System.currentTimeMillis()
		protected set

	@LastModifiedDate
	@Column(nullable = false)
	var updateTimestamp: Long = System.currentTimeMillis()
		protected set
}