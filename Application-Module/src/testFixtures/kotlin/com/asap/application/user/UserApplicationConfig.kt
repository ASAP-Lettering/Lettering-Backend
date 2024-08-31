package com.asap.application.user

import com.asap.application.user.port.out.UserTokenManagementPort
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class UserApplicationConfig(
    private val userTokenManagementPort: UserTokenManagementPort
) {

    @Bean
    fun userMockGenerator(): UserMockManager {
        return UserMockManager(userTokenManagementPort)
    }


}