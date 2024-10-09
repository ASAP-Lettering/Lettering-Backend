package com.asap.bootstrap.integration.security

import com.asap.bootstrap.common.security.annotation.AccessUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/security")
class TestSecurityController {
    @GetMapping
    fun testSecurity(): String = "test"

    @GetMapping("/secured")
    fun testSecured(
        @AccessUser userId: String,
    ): String = "secured"
}
