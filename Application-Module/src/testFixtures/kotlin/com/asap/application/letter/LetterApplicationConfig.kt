package com.asap.application.letter

import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class LetterApplicationConfig(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort,
    private val spaceLetterManagementPort: SpaceLetterManagementPort,
    private val draftLetterManagementPort: DraftLetterManagementPort,
    private val receiveDraftLetterManagementPort: ReceiveDraftLetterManagementPort,
) {
    @Bean
    fun letterMockManager(): LetterMockManager =
        LetterMockManager(
            sendLetterManagementPort,
            independentLetterManagementPort,
            spaceLetterManagementPort,
            draftLetterManagementPort,
            receiveDraftLetterManagementPort
        )
}
