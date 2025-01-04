package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.LetterLogUsecase
import com.asap.application.letter.port.out.LetterLogManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.LetterFixture
import com.asap.domain.letter.entity.LetterLogType
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LetterLogServiceTest : BehaviorSpec({

    val letterLogManagementPort = mockk<LetterLogManagementPort>(relaxed = true)
    val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed = true)

    val letterLogService = LetterLogService(
        letterLogManagementPort = letterLogManagementPort,
        sendLetterManagementPort = mockSendLetterManagementPort,
    )

    given("편지 관련 로그 서비스 테스트") {
        val request = LetterLogUsecase.LogRequest(
            letterCode = "letterCode",
            logType = LetterLogType.SHARE,
            logContent = "logContent",
        )

        val mockSendLetter = LetterFixture.generateSendLetter()

        every { mockSendLetterManagementPort.getLetterByCodeNotNull(any()) } returns mockSendLetter

        `when`("편지를 저장 요청하면") {
            letterLogService.log(request)
            then("편지를 저장한다") {
                verify { letterLogManagementPort.save(any()) }
            }
        }
    }
}) {
}