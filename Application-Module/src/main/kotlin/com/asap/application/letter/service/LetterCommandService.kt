package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.application.letter.port.`in`.VerifyLetterAccessibleUsecase
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.service.LetterCodeGenerator
import org.springframework.stereotype.Service

@Service
class LetterCommandService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val userManagementPort: UserManagementPort
) : SendLetterUsecase, VerifyLetterAccessibleUsecase {

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

    override fun verify(command: VerifyLetterAccessibleUsecase.Command): VerifyLetterAccessibleUsecase.Response {
        if (sendLetterManagementPort.verifiedLetter(DomainId(command.userId), command.letterCode)) {
            val sendLetter = sendLetterManagementPort.getExpiredLetterNotNull(
                receiverId = DomainId(command.userId),
                command.letterCode
            )
            return VerifyLetterAccessibleUsecase.Response(letterId = sendLetter.id.value)
        }

        val sendLetter = sendLetterManagementPort.getLetterByCodeNotNull(command.letterCode)
        sendLetter.isSameReceiver {
            userManagementPort.getUserNotNull(DomainId(command.userId)).username
        }.takeIf { it }?.let {
            sendLetterManagementPort.expireLetter(
                receiverId = DomainId(command.userId),
                letterId = sendLetter.id
            )
            return VerifyLetterAccessibleUsecase.Response(letterId = sendLetter.id.value)
        } ?: throw LetterException.InvalidLetterAccessException()
    }
}