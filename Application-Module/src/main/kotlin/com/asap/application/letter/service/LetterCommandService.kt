package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.AddLetterUsecase
import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.application.letter.port.`in`.VerifyLetterAccessibleUsecase
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.service.LetterCodeGenerator
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LetterCommandService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort,
    private val userManagementPort: UserManagementPort,
) : SendLetterUsecase, VerifyLetterAccessibleUsecase, AddLetterUsecase {

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

    override fun addVerifiedLetter(command: AddLetterUsecase.Command.VerifyLetter) {
        val sendLetter = sendLetterManagementPort.getExpiredLetterNotNull(
            receiverId = DomainId(command.userId),
            letterId = DomainId(command.letterId)
        )
        val independentLetter = IndependentLetter(
            senderId = sendLetter.senderId,
            receiverId = DomainId(command.userId),
            content = sendLetter.content,
            images = sendLetter.images,
            templateType = sendLetter.templateType,
            receiveDate = sendLetter.createdDate,
            senderName = userManagementPort.getUserNotNull(sendLetter.senderId).username
        )
        independentLetterManagementPort.save(independentLetter)
        sendLetterManagementPort.remove(sendLetter.id)
    }

    override fun addPhysicalLetter(command: AddLetterUsecase.Command.AddPhysicalLetter) {
        val independentLetter = IndependentLetter(
            senderName = command.senderName,
            receiverId = DomainId(command.userId),
            content = command.content,
            images = command.images,
            templateType = command.templateType,
            receiveDate = LocalDate.now(),
        )
        independentLetterManagementPort.save(independentLetter)
    }
}