package com.asap.application.letter.service

import com.asap.application.letter.event.DraftLetterSendEvent
import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.service.LetterCodeGenerator
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class LetterCommandService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort,
    private val spaceLetterManagementPort: SpaceLetterManagementPort,
    private val userManagementPort: UserManagementPort,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : SendLetterUsecase,
    VerifyLetterAccessibleUsecase,
    AddLetterUsecase,
    MoveLetterUsecase,
    RemoveLetterUsecase {
    private val letterCodeGenerator = LetterCodeGenerator()

    override fun send(command: SendLetterUsecase.Command): SendLetterUsecase.Response {
        val sendLetter =
            SendLetter(
                receiverName = command.receiverName,
                content =
                    LetterContent(
                        content = command.content,
                        templateType = command.templateType,
                        images = command.images,
                    ),
                senderId = DomainId(command.userId),
                letterCode =
                    letterCodeGenerator.generateCode(
                        content = command.content,
                        ownerId = command.userId,
                    ),
            )
        sendLetterManagementPort.save(sendLetter)

        command.draftId?.let {
            applicationEventPublisher.publishEvent(DraftLetterSendEvent(it, sendLetter.senderId.value))
        }

        return SendLetterUsecase.Response(letterCode = sendLetter.letterCode!!)
    }

    override fun verify(command: VerifyLetterAccessibleUsecase.Command): VerifyLetterAccessibleUsecase.Response {
        if (sendLetterManagementPort.verifiedLetter(DomainId(command.userId), command.letterCode)) {
            val sendLetter =
                sendLetterManagementPort.getReadLetterNotNull(
                    receiverId = DomainId(command.userId),
                    command.letterCode,
                )
            return VerifyLetterAccessibleUsecase.Response(letterId = sendLetter.id.value)
        }

        val sendLetter = sendLetterManagementPort.getLetterByCodeNotNull(command.letterCode)
        sendLetter
            .isSameReceiver {
                userManagementPort.getUserNotNull(DomainId(command.userId))
            }.takeIf { it }
            ?.let {
                sendLetter.readLetter(DomainId(command.userId))
                sendLetterManagementPort.save(sendLetter)
                return VerifyLetterAccessibleUsecase.Response(letterId = sendLetter.id.value)
            } ?: throw LetterException.InvalidLetterAccessException()
    }

    override fun addVerifiedLetter(command: AddLetterUsecase.Command.VerifyLetter) {
        val sendLetter =
            sendLetterManagementPort.getReadLetterNotNull(
                receiverId = DomainId(command.userId),
                letterId = DomainId(command.letterId),
            )
        val independentLetter =
            IndependentLetter(
                sender =
                    SenderInfo(
                        senderId = sendLetter.senderId,
                        senderName = userManagementPort.getUserNotNull(sendLetter.senderId).username,
                    ),
                receiver =
                    ReceiverInfo(
                        receiverId = DomainId(command.userId),
                    ),
                content = sendLetter.content,
                receiveDate = sendLetter.createdDate,
            )
        sendLetter.receiveLetter()

        sendLetterManagementPort.save(sendLetter)
        independentLetterManagementPort.save(independentLetter)
    }

    override fun addPhysicalLetter(command: AddLetterUsecase.Command.AddPhysicalLetter) {
        val independentLetter =
            IndependentLetter(
                sender =
                    SenderInfo(
                        senderName = command.senderName,
                    ),
                receiver =
                    ReceiverInfo(
                        receiverId = DomainId(command.userId),
                    ),
                content =
                    LetterContent(
                        content = command.content,
                        templateType = command.templateType,
                        images = command.images,
                    ),
                receiveDate = LocalDate.now(),
            )
        independentLetterManagementPort.save(independentLetter)
    }

    override fun moveToSpace(command: MoveLetterUsecase.Command.ToSpace) {
        val independentLetter =
            independentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId(command.letterId))
        spaceLetterManagementPort.saveByIndependentLetter(
            independentLetter,
            DomainId(command.spaceId),
            DomainId(command.userId),
        )
    }

    override fun moveToIndependent(command: MoveLetterUsecase.Command.ToIndependent) {
        spaceLetterManagementPort
            .getSpaceLetterNotNull(
                DomainId(command.letterId),
                DomainId(command.userId),
            ).apply {
                val independentLetter = IndependentLetter.createBySpaceLetter(this, DomainId(command.userId))
                independentLetterManagementPort.save(independentLetter)
            }
    }

    override fun removeSpaceLetter(command: RemoveLetterUsecase.Command.SpaceLetter) {
        val spaceLetter =
            spaceLetterManagementPort.getSpaceLetterNotNull(
                DomainId(command.letterId),
                DomainId(command.userId),
            )
        spaceLetterManagementPort.delete(spaceLetter)
    }
}
