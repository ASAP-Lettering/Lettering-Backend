package com.asap.domain.letter.service

import java.security.MessageDigest
import java.util.*

class LetterCodeGenerator {

    fun generateCode(
        content: String,
        ownerId: String
    ): String {
        val salt = UUID.randomUUID().toString()
        val input = content + ownerId + salt
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        val hexString = StringBuffer()
        for (value in hash) {
            val hex = Integer.toHexString(0xff and value.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        val letterCode = hexString.toString()
        return letterCode
    }
}