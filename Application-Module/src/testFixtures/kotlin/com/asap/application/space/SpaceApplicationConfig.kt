package com.asap.application.space

import com.asap.application.space.port.out.SpaceManagementPort
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class SpaceApplicationConfig(
    private val spaceManagementPort: SpaceManagementPort,
) {
    @Bean
    fun spaceMockManager(): SpaceMockManager = SpaceMockManager(spaceManagementPort)
}
