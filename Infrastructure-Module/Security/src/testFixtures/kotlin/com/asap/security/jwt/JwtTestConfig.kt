package com.asap.security.jwt

import com.asap.security.jwt.user.UserJwtProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class JwtTestConfig {

    @Bean
    @Primary
    fun userJwtProperties(): UserJwtProperties {
        return UserJwtProperties(TEST_SECRET)
    }

    @Bean
    fun testJwtDataGenerator(userJwtProperties: UserJwtProperties): TestJwtDataGenerator {
        return TestJwtDataGenerator(userJwtProperties)
    }


    companion object {
        const val TEST_SECRET = "hdcksljdfaklsdjfnakjcbvzcnxvbaikaklsjdflhiuasdvbzmxncbvaksd"
    }
}