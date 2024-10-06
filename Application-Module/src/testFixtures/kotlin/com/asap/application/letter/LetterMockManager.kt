package com.asap.application.letter

import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.entity.SpaceLetter
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
) {
    private val letterCodeGenerator = LetterCodeGenerator()

    fun generateMockSendLetter(
        receiverName: String,
        senderId: String = DomainId.generate().value,
    ): Map<String, Any> {
        val sendLetter =
            SendLetter(
                receiverName = receiverName,
                content =
                    LetterContent(
                        content = "content",
                        templateType = 1,
                        images = listOf("image1", "image2"),
                    ),
                senderId = DomainId(senderId),
                letterCode =
                    letterCodeGenerator.generateCode(
                        content = "content",
                        ownerId = senderId,
                    ),
            )
        sendLetterManagementPort.save(sendLetter)
        return mapOf(
            "letterCode" to sendLetter.letterCode!!,
            "letterId" to sendLetter.id.value,
        )
    }

    fun generateMockReadLetter(
        receiverName: String,
        receiverId: String,
        senderId: String = DomainId.generate().value,
    ): Map<String, Any> {
        val sendLetter =
            SendLetter(
                receiverName = receiverName,
                content =
                    LetterContent(
                        content = "content",
                        templateType = 1,
                        images = listOf("image1", "image2"),
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
    ): Map<String, Any> {
        val independentLetter =
            IndependentLetter(
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
                        images = listOf("image1", "image2"),
                    ),
                receiveDate = LocalDate.now(),
                movedAt = movedAt,
                isOpened = isOpened,
            )
        independentLetterManagementPort.save(independentLetter)
        return mapOf(
            "letterId" to independentLetter.id.value,
        )
    }

    fun generateMockSpaceLetter(
        senderId: String? = null,
        receiverId: String,
        senderName: String,
        spaceId: String,
    ): Map<String, Any> {
        val spaceLetter =
            SpaceLetter(
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
                        images = listOf("image1", "image2"),
                    ),
                spaceId = DomainId(spaceId),
                receiveDate = LocalDate.now(),
            )
        spaceLetterManagementPort.save(
            spaceLetter,
        )
        return mapOf(
            "letterId" to spaceLetter.id.value,
            "senderName" to senderName,
        )
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
}
