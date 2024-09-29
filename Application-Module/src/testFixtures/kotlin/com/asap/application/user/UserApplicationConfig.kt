package com.asap.application.user

import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenManagementPort
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class UserApplicationConfig(
    private val userTokenManagementPort: UserTokenManagementPort,
    private val userManagementPort: UserManagementPort,
    private val userAuthManagementPort: UserAuthManagementPort,
) {
    @Bean
    fun userMockGenerator(): UserMockManager = UserMockManager(userTokenManagementPort, userManagementPort, userAuthManagementPort)
}
