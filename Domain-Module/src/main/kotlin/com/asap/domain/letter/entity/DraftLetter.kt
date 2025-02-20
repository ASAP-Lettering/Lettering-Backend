package com.asap.domain.letter.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import java.time.LocalDateTime

class DraftLetter(
    id: DomainId,
    var content: String,
    var receiverName: String,
    val ownerId: DomainId,
    var images: List<String>,
    var lastUpdated: LocalDateTime,
    createdAt: LocalDateTime,
) : Aggregate<DraftLetter>(id, createdAt, lastUpdated) {
    companion object {
        fun default(ownerId: DomainId) =
            DraftLetter(
                id = DomainId.generate(),
                ownerId = ownerId,
                content = "",
                receiverName = "",
                images = emptyList(),
                lastUpdated = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
            )
    }

    fun update(
        content: String,
        receiverName: String,
        images: List<String>,
    ) {
        this.content = content
        this.receiverName = receiverName
        this.images = images
        this.lastUpdated = LocalDateTime.now()
        updateTime()
    }
}
