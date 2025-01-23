package com.asap.domain.letter.entity

import com.asap.domain.common.BaseEntity
import com.asap.domain.common.DomainId
import java.time.LocalDateTime

class ReceiveDraftLetter(
    id: DomainId,
    var content: String,
    var senderName: String,
    val ownerId: DomainId,
    var images: List<String>,
    var lastUpdated: LocalDateTime = LocalDateTime.now(),
    val type: ReceiveDraftLetterType,
) : BaseEntity() {
    companion object {
        fun default(ownerId: DomainId) =
            ReceiveDraftLetter(
                id = DomainId.generate(),
                ownerId = ownerId,
                content = "",
                senderName = "",
                images = emptyList(),
                type = ReceiveDraftLetterType.PHYSICAL,
            )
    }

    fun update(
        content: String,
        senderName: String,
        images: List<String>,
    ) {
        this.content = content
        this.senderName = senderName
        this.images = images
        this.lastUpdated = LocalDateTime.now()
    }
}

enum class ReceiveDraftLetterType{
    PHYSICAL,
}