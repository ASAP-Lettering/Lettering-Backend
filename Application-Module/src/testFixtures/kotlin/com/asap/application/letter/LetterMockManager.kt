package com.asap.application.letter

import com.asap.application.letter.port.out.*
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.*
import com.asap.domain.letter.service.LetterCodeGenerator
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import java.time.LocalDate
import java.time.LocalDateTime

class LetterMockManager(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort,
    private val spaceLetterManagementPort: SpaceLetterManagementPort,
    private val draftLetterManagementPort: DraftLetterManagementPort,
    private val receiveDraftLetterManagementPort: ReceiveDraftLetterManagementPort,
) {
    private val letterCodeGenerator = LetterCodeGenerator()

    fun generateMockSendLetter(
        receiverName: String,
        senderId: String = DomainId.generate().value,
    ): SendLetter {
        val sendLetter =
            SendLetter.create(
                receiverName = receiverName,
                content =
                    LetterContent(
                        content = "content",
                        templateType = 1,
                        images = mutableListOf("image1", "image2"),
                    ),
                senderId = DomainId(senderId),
                letterCode =
                    letterCodeGenerator.generateCode(
                        content = "content",
                        ownerId = senderId,
                    ),
            )
        sendLetterManagementPort.save(sendLetter)
        return sendLetter
    }

    fun generateMockReadLetter(
        receiverName: String,
        receiverId: String,
        senderId: String = DomainId.generate().value,
    ): Map<String, Any> {
        val sendLetter =
            SendLetter.create(
                receiverName = receiverName,
                content =
                    LetterContent(
                        content = "content",
                        templateType = 1,
                        images = mutableListOf("image1", "image2"),
                    ),
                senderId = DomainId(senderId),
                letterCode =
                    letterCodeGenerator.generateCode(
                        content = "content",
                        ownerId = senderId,
                    ),
            )
        sendLetter.readLetter(DomainId(receiverId))
        sendLetterManagementPort.save(sendLetter)
        return mapOf(
            "letterCode" to sendLetter.letterCode!!,
            "letterId" to sendLetter.id.value,
        )
    }

    fun generateMockIndependentLetter(
        senderId: String? = null,
        receiverId: String,
        senderName: String,
        movedAt: LocalDateTime = LocalDateTime.now(),
        isOpened: Boolean = false,
    ): IndependentLetter {
        val independentLetter =
            IndependentLetter.create(
                sender =
                    SenderInfo(
                        senderId = senderId?.let { DomainId(it) },
                        senderName = senderName,
                    ),
                receiver =
                    ReceiverInfo(
                        receiverId = DomainId(receiverId),
                    ),
                content =
                    LetterContent(
                        content = "content",
                        templateType = 1,
                        images = mutableListOf("image1", "image2"),
                    ),
                receiveDate = LocalDate.now(),
                movedAt = movedAt,
                isOpened = isOpened,
            )
        independentLetterManagementPort.save(independentLetter)
        return independentLetter
    }

    fun generateMockSpaceLetter(
        senderId: String? = null,
        receiverId: String,
        senderName: String,
        spaceId: String,
    ): SpaceLetter {
        val spaceLetter =
            SpaceLetter.create(
                sender =
                    SenderInfo(
                        senderId = senderId?.let { DomainId(it) },
                        senderName = senderName,
                    ),
                receiver =
                    ReceiverInfo(
                        receiverId = DomainId(receiverId),
                    ),
                content =
                    LetterContent(
                        content = "content",
                        templateType = 1,
                        images = mutableListOf("image1", "image2"),
                    ),
                spaceId = DomainId(spaceId),
                receiveDate = LocalDate.now(),

            )
        spaceLetterManagementPort.save(
            spaceLetter,
        )
        return spaceLetter
    }

    fun isExistSpaceLetter(
        letterId: String,
        userId: String,
    ): Boolean {
        return try {
            spaceLetterManagementPort.getSpaceLetterNotNull(DomainId(letterId), DomainId(userId))
            true
        } catch (e: Exception) {
            return false
        }
    }

    fun generateMockDraftLetter(userId: String): String {
        val draftLetter = DraftLetter.default(DomainId(userId))
        draftLetterManagementPort.save(draftLetter)
        return draftLetter.id.value
    }

    fun generateMockReceiveDraftLetter(userId: String): String {
        val receiveDraftLetter = ReceiveDraftLetter.default(DomainId(userId))
        receiveDraftLetterManagementPort.save(receiveDraftLetter)
        return receiveDraftLetter.id.value
    }
}
