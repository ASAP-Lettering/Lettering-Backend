package com.asap.persistence.jpa.letter

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import com.asap.persistence.jpa.letter.entity.DraftLetterEntity

object DraftLetterMapper {
    fun toEntity(draftLetter: DraftLetter): DraftLetterEntity =
        DraftLetterEntity(
            id = draftLetter.id.value,
            content = draftLetter.content,
            receiverName = draftLetter.receiverName,
            ownerId = draftLetter.ownerId.value,
            images = draftLetter.images,
        )

    fun toDomain(draftLetterEntity: DraftLetterEntity): DraftLetter =
        DraftLetter(
            id = DomainId(draftLetterEntity.id),
            content = draftLetterEntity.content,
            receiverName = draftLetterEntity.receiverName,
            ownerId = DomainId(draftLetterEntity.ownerId),
            images = draftLetterEntity.images,
        )
}
