package com.asap.domain

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.domain.letter.enums.LetterStatus
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import java.time.LocalDate
import java.time.LocalDateTime

object LetterFixture {
    fun generateSendLetter(
        senderId: DomainId = DomainId.generate(),
        receiverName: String = "receiverName",
        letterCode: String = "letterCode",
        status: LetterStatus = LetterStatus.READ,
        receiverId: DomainId = DomainId.generate(),
    ): SendLetter =
        SendLetter.create(
            receiverName = receiverName,
            content =
                LetterContent(
                    content = "content",
                    templateType = 1,
                    images = mutableListOf("image1", "image2"),
                ),
            senderId = senderId,
            letterCode = letterCode,
            status = status,
            receiverId = receiverId,
        )
    
    fun generateAnonymousSendLetter(
        senderName: String = "Anonymous",
        receiverName: String = "receiverName",
        letterCode: String = "letterCode",
        status: LetterStatus = LetterStatus.READ,
        receiverId: DomainId = DomainId.generate(),
    ): SendLetter =
        SendLetter.createAnonymous(
            content =
                LetterContent(
                    content = "content",
                    templateType = 1,
                    images = mutableListOf("image1", "image2"),
                ),
            receiverName = receiverName,
            letterCode = letterCode,
            senderName = senderName,
            status = status,
            receiverId = receiverId,
        )

    fun generateIndependentLetter(
        id: DomainId = DomainId.generate(),
        content: String = "content",
        senderId: DomainId? = DomainId.generate(),
        senderName: String = "senderName",
        receiverId: DomainId = DomainId.generate(),
        receiveDate: LocalDate = LocalDate.now(),
        movedAt: LocalDateTime = LocalDateTime.now(),
        isOpened: Boolean = false,
    ) = IndependentLetter.create(
        id = id,
        content =
            LetterContent(
                content = content,
                templateType = 1,
                images = mutableListOf("image1", "image2"),
            ),
        sender =
            SenderInfo(
                senderId = senderId,
                senderName = senderName,
            ),
        receiver =
            ReceiverInfo(
                receiverId = receiverId,
            ),
        receiveDate = receiveDate,
        movedAt = movedAt,
        isOpened = isOpened,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    fun generateSpaceLetter(
        id: DomainId = DomainId.generate(),
        spaceId: DomainId = DomainId.generate(),
        senderName: String = "senderName",
        content: String = "content",
        images: List<String> = listOf("image1", "image2"),
        receiveDate: LocalDate = LocalDate.now(),
        movedAt: LocalDateTime = LocalDateTime.now(),
        senderId: DomainId? = DomainId.generate(),
        receiverId: DomainId = DomainId.generate(),
    ) = SpaceLetter.create(
        id = id,
        spaceId = spaceId,
        sender =
            SenderInfo(
                senderId = senderId,
                senderName = senderName,
            ),
        receiver =
            ReceiverInfo(
                receiverId = receiverId,
            ),
        content =
            LetterContent(
                content = content,
                templateType = 1,
                images = images.toMutableList(),
            ),
        receiveDate = receiveDate,
        movedAt = movedAt,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )
}
