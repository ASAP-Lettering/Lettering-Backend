package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.application.letter.port.out.SendLetterManagementPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.mockk
import io.mockk.verify


class LetterCommandServiceTest:BehaviorSpec({

    val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed=true)

    val letterCommandService = LetterCommandService(mockSendLetterManagementPort)



    given("편지 전송 요청이 들어올 때"){
        val command = SendLetterUsecase.Command(
            userId = "user-id",
            receiverName = "receiver-name",
            content = "content",
            images = emptyList(),
            templateType = 1,
            draftId = null
        )
        `when`("편지 전송 요청을 처리하면"){
            val response = letterCommandService.send(command)
            then("편지 코드가 생성되고, 편지가 저장되어야 한다"){
                response.letterCode shouldNotBeNull {
                    this.isNotBlank()
                    this.isNotEmpty()
                }
                verify { mockSendLetterManagementPort.save(any()) }
            }
        }
    }
}) {
}