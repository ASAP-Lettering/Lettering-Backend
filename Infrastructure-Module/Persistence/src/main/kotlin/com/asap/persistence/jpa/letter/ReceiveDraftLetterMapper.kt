package com.asap.persistence.jpa.letter

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.ReceiveDraftLetter
import com.asap.persistence.jpa.letter.entity.ReceiveDraftLetterEntity

object ReceiveDraftLetterMapper {
    fun toEntity(receiveDraftLetter: ReceiveDraftLetter): ReceiveDraftLetterEntity =
        ReceiveDraftLetterEntity(
            id = receiveDraftLetter.id.value,
            content = receiveDraftLetter.content,
            senderName = receiveDraftLetter.senderName,
            ownerId = receiveDraftLetter.ownerId.value,
            images = receiveDraftLetter.images,
            type = receiveDraftLetter.type,
            createdAt = receiveDraftLetter.createdAt,
            updatedAt = receiveDraftLetter.lastUpdated,
        )

    fun toDomain(receiveDraftLetterEntity: ReceiveDraftLetterEntity): ReceiveDraftLetter =
        ReceiveDraftLetter(
            id = DomainId(receiveDraftLetterEntity.id),
            content = receiveDraftLetterEntity.content,
            senderName = receiveDraftLetterEntity.senderName,
            ownerId = DomainId(receiveDraftLetterEntity.ownerId),
            images = receiveDraftLetterEntity.images,
            lastUpdated = receiveDraftLetterEntity.updatedAt,
            type = receiveDraftLetterEntity.type,
            createdAt = receiveDraftLetterEntity.createdAt,
        )
}