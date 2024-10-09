package com.asap.persistence.jpa.letter

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import com.asap.persistence.jpa.letter.entity.ReceiveLetterEntity

object ReceiverLetterMapper {
    fun toIndependentLetter(receiveLetterEntity: ReceiveLetterEntity): IndependentLetter =
        IndependentLetter(
            id = DomainId(receiveLetterEntity.id),
            content =
                LetterContent(
                    content = receiveLetterEntity.content,
                    templateType = receiveLetterEntity.templateType,
                    images = receiveLetterEntity.images.toMutableList(),
                ),
            sender =
                SenderInfo(
                    senderId = receiveLetterEntity.senderId?.let { DomainId(it) },
                    senderName = receiveLetterEntity.senderName,
                ),
            receiver =
                ReceiverInfo(
                    receiverId = DomainId(receiveLetterEntity.receiverId),
                ),
            receiveDate = receiveLetterEntity.receiveDate,
            movedAt = receiveLetterEntity.movedAt,
            isOpened = receiveLetterEntity.isOpened,
        )

    fun toSpaceLetter(receiveLetterEntity: ReceiveLetterEntity): SpaceLetter =
        SpaceLetter(
            id = DomainId(receiveLetterEntity.id),
            content =
                LetterContent(
                    content = receiveLetterEntity.content,
                    templateType = receiveLetterEntity.templateType,
                    images = receiveLetterEntity.images.toMutableList(),
                ),
            sender =
                SenderInfo(
                    senderId = receiveLetterEntity.senderId?.let { DomainId(it) },
                    senderName = receiveLetterEntity.senderName,
                ),
            receiver =
                ReceiverInfo(
                    receiverId = DomainId(receiveLetterEntity.receiverId),
                ),
            receiveDate = receiveLetterEntity.receiveDate,
            spaceId = receiveLetterEntity.spaceId!!.let { DomainId(it) },
        )
}
