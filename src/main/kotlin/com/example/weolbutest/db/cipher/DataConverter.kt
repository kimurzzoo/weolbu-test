package com.example.weolbutest.db.cipher

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Convert
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Convert
@Component
class DataConverter : AttributeConverter<String, String> {

	private val encoder = Base64.getEncoder()
	private val decoder = Base64.getDecoder()

	@Value("\${cipher.secret-key}")
	private lateinit var secretKey: String

	fun cipherPkcs5(opMode: Int, secretKey: String): Cipher {
		val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
		val sk = SecretKeySpec(
			secretKey.toByteArray(Charsets.UTF_8),
			"AES"
		)
		val iv = IvParameterSpec(
			secretKey.substring(0, 16).toByteArray(Charsets.UTF_8)
		)

		c.init(opMode, sk, iv)
		return c
	}

	override fun convertToDatabaseColumn(attribute: String?): String {
		val encryptedString = this.cipherPkcs5(Cipher.ENCRYPT_MODE, secretKey)
			.doFinal(attribute?.toByteArray(Charsets.UTF_8)) // encryption

		return String(encoder.encode(encryptedString)) // base 54 encoding
	}

	override fun convertToEntityAttribute(dbData: String?): String? {
		val byteString = decoder.decode(dbData?.toByteArray(Charsets.UTF_8)) // base 64 decoding
		return String(
			this.cipherPkcs5(Cipher.DECRYPT_MODE, secretKey).doFinal(byteString)
		) // decryption
	}
}