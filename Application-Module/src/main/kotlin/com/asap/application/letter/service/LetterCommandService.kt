package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.domain.letter.service.LetterCodeGenerator
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
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
) : SendLetterUsecase,
    VerifyLetterAccessibleUsecase,
    AddLetterUsecase,
    MoveLetterUsecase,
    RemoveLetterUsecase,
    UpdateLetterUsecase {
    private val letterCodeGenerator = LetterCodeGenerator()

    override fun send(command: SendLetterUsecase.Command): SendLetterUsecase.Response {
        val sendLetter =
            SendLetter.create(
                receiverName = command.receiverName,
                content =
                    LetterContent(
                        content = command.content,
                        templateType = command.templateType,
                        images = command.images.toMutableList(),
                    ),
                senderId = DomainId(command.userId),
                letterCode =
                    letterCodeGenerator.generateCode(
                        content = command.content,
                        ownerId = command.userId,
                    ),
                draftId = command.draftId?.let { DomainId(it) },
            )
        sendLetterManagementPort.save(sendLetter)

        return SendLetterUsecase.Response(letterCode = sendLetter.letterCode!!)
    }

    override fun sendAnonymous(command: SendLetterUsecase.AnonymousCommand): SendLetterUsecase.Response {
        val sendLetter =
            SendLetter.createAnonymous(
                receiverName = command.receiverName,
                content =
                    LetterContent(
                        content = command.content,
                        templateType = command.templateType,
                        images = command.images.toMutableList(),
                    ),
                letterCode =
                    letterCodeGenerator.generateCode(
                        content = command.content,
                    ),
                senderName = command.senderName ?: ANONYMOUS_SENDER_NAME,
            )

        sendLetterManagementPort.save(sendLetter)

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
        val receiver = userManagementPort.getUserNotNull(DomainId(command.userId))

        if (sendLetter.isSameReceiver(receiver)) {
            sendLetter.readLetter(DomainId(command.userId))
            sendLetterManagementPort.save(sendLetter)
            return VerifyLetterAccessibleUsecase.Response(letterId = sendLetter.id.value)
        }

        throw LetterException.InvalidLetterAccessException()
    }

    override fun addVerifiedLetter(command: AddLetterUsecase.Command.VerifyLetter) {
        val sendLetter =
            sendLetterManagementPort.getReadLetterNotNull(
                receiverId = DomainId(command.userId),
                letterId = DomainId(command.letterId),
            )

        val independentLetter =
            IndependentLetter.create(
                sender =
                    SenderInfo(
                        senderId = sendLetter.senderId,
                        senderName = sendLetter.senderName ?: ANONYMOUS_SENDER_NAME,
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
            IndependentLetter.create(
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
                        images = command.images.toMutableList(),
                    ),
                receiveDate = LocalDate.now(),
                draftId = command.draftId?.let { DomainId(it) },
            )
        independentLetterManagementPort.save(independentLetter)
    }

    override fun addAnonymousLetter(command: AddLetterUsecase.Command.AddAnonymousLetter) {
        val sendLetter = sendLetterManagementPort.getLetterByCodeNotNull(command.letterCode)
        val user = userManagementPort.getUserNotNull(DomainId(command.userId))

        sendLetter.configSenderId(user.id)
        sendLetterManagementPort.save(sendLetter)
    }

    override fun moveToSpace(command: MoveLetterUsecase.Command.ToSpace) {
        independentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId(command.letterId)).apply {
            val spaceLetter = SpaceLetter.createByIndependentLetter(this, DomainId(command.spaceId))

            spaceLetterManagementPort.save(spaceLetter)
        }
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

    override fun removeSpaceLetterBy(command: RemoveLetterUsecase.Command.SpaceId) {
        spaceLetterManagementPort
            .getAllBy(
                spaceId = DomainId(command.spaceId),
                userId = DomainId(command.userId),
            ).forEach {
                spaceLetterManagementPort.delete(it)
            }
    }

    override fun removeSenderLetterBy(command: RemoveLetterUsecase.Command.SendLetter) {
        sendLetterManagementPort
            .getSendLetterBy(
                letterId = DomainId(command.letterId),
                senderId = DomainId(command.userId),
            ).apply {
                delete()
                sendLetterManagementPort.save(this)
                sendLetterManagementPort.delete(this)
            }
    }

    override fun removeIndependentLetter(command: RemoveLetterUsecase.Command.IndependentLetter) {
        independentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId(command.letterId)).apply {
            delete()
            independentLetterManagementPort.delete(this)
        }
    }

    override fun removeAllIndependentLetterBy(command: RemoveLetterUsecase.Command.User) {
        independentLetterManagementPort
            .getAllByReceiverId(DomainId(command.userId))
            .forEach {
                it.delete()
                independentLetterManagementPort.save(it)
                independentLetterManagementPort.delete(it)
            }
    }

    override fun removeAllSenderLetterBy(command: RemoveLetterUsecase.Command.User) {
        sendLetterManagementPort
            .getAllBy(DomainId(command.userId))
            .forEach {
                it.delete()
                sendLetterManagementPort.save(it)
                sendLetterManagementPort.delete(it)
            }
    }

    override fun removeAllSenderLetterBy(command: RemoveLetterUsecase.Command.SendLetters) {
        sendLetterManagementPort
            .getAllBy(DomainId(command.userId), command.letterIds.map { DomainId(it) })
            .forEach {
                it.delete()
                sendLetterManagementPort.save(it)
                sendLetterManagementPort.delete(it)
            }
    }

    override fun updateIndependentLetter(command: UpdateLetterUsecase.Command.Independent) {
        val independentLetter =
            independentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId(command.letterId))
        independentLetter.update(
            senderName = command.senderName,
            content = command.content,
            images = command.images,
            templateType = command.templateType,
        )
        independentLetterManagementPort.save(independentLetter)
    }

    override fun updateSpaceLetter(command: UpdateLetterUsecase.Command.Space) {
        val spaceLetter =
            spaceLetterManagementPort.getSpaceLetterNotNull(
                DomainId(command.letterId),
                DomainId(command.userId),
            )
        spaceLetter.update(
            senderName = command.senderName,
            content = command.content,
            images = command.images,
            templateType = command.templateType,
        )
        spaceLetterManagementPort.save(spaceLetter)
    }

    companion object {
        private const val ANONYMOUS_SENDER_NAME = "Anonymous"
    }
}
