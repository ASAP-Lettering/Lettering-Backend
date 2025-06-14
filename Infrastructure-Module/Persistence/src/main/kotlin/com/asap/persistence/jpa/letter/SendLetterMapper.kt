package com.asap.persistence.jpa.letter

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.vo.LetterContent
import com.asap.persistence.jpa.letter.entity.SendLetterEntity

object SendLetterMapper {
    fun toSendLetter(sendLetterEntity: SendLetterEntity): SendLetter =
        SendLetter(
            id = DomainId(sendLetterEntity.id),
            content =
                LetterContent(
                    content = sendLetterEntity.content,
                    images = sendLetterEntity.images.toMutableList(),
                    templateType = sendLetterEntity.templateType,
                ),
            receiverName = sendLetterEntity.receiverName,
            letterCode = sendLetterEntity.letterCode ?: "",
            senderId = sendLetterEntity.senderId?.let { DomainId(it) },
            senderName = sendLetterEntity.senderName,
            receiverId = sendLetterEntity.receiverId?.let { DomainId(it) },
            status = sendLetterEntity.letterStatus,
            createdAt = sendLetterEntity.createdAt,
            updatedAt = sendLetterEntity.updatedAt,
        )

    fun toSendLetterEntity(sendLetter: SendLetter): SendLetterEntity =
        SendLetterEntity(
            id = sendLetter.id.value,
            content = sendLetter.content.content,
            images = sendLetter.content.images,
            templateType = sendLetter.content.templateType,
            receiverName = sendLetter.receiverName,
            senderId = sendLetter.senderId?.value,
            senderName = sendLetter.senderName,
            letterCode = sendLetter.letterCode,
            receiverId = sendLetter.receiverId?.value,
            letterStatus = sendLetter.status,
            createdAt = sendLetter.createdAt,
            updatedAt = sendLetter.updatedAt,
        )
}
