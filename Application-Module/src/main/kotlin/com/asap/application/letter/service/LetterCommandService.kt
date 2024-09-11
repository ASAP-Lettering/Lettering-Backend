package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.service.LetterCodeGenerator
import org.springframework.stereotype.Service

@Service
class LetterCommandService(
    private val sendLetterManagementPort: SendLetterManagementPort
) : SendLetterUsecase {

    private val letterCodeGenerator = LetterCodeGenerator()


    override fun send(command: SendLetterUsecase.Command): SendLetterUsecase.Response {
        val sendLetter = SendLetter(
            receiverName = command.receiverName,
            content = command.content,
            images = command.images,
            templateType = command.templateType,
            senderId = DomainId(command.userId),
            letterCode = letterCodeGenerator.generateCode(
                content = command.content,
                ownerId = command.userId
            )
        )
        sendLetterManagementPort.save(sendLetter)

        command.draftId?.let {
            // event
        }

        return SendLetterUsecase.Response(letterCode = sendLetter.letterCode)
    }
}