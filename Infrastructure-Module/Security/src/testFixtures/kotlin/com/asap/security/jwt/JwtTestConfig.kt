package com.asap.security.jwt

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.security.jwt.user.UserJwtProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class JwtTestConfig(
    private val userTokenManagementPort: UserTokenManagementPort,
) {
    @Bean
    @Primary
    fun userJwtProperties(): UserJwtProperties = UserJwtProperties(TEST_SECRET)

    @Bean
    fun testJwtDataGenerator(userJwtProperties: UserJwtProperties): JwtMockManager =
        JwtMockManager(userJwtProperties, userTokenManagementPort)

    companion object {
        const val TEST_SECRET = "hdcksljdfaklsdjfnakjcbvzcnxvbaikaklsjdflhiuasdvbzmxncbvaksd"
    }
}
