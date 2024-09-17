package com.asap.application.letter

import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class LetterApplicationConfig(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort,
    private val spaceLetterManagementPort: SpaceLetterManagementPort
) {

    @Bean
    fun letterMockManager(): LetterMockManager {
        return LetterMockManager(sendLetterManagementPort,independentLetterManagementPort,spaceLetterManagementPort)
    }
}