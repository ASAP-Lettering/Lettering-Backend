package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import java.time.LocalDateTime

data class DraftLetter(
    val id: DomainId = DomainId.generate(),
    var content: String,
    var receiverName: String,
    val ownerId: DomainId,
    var images: List<String>,
    var lastUpdated: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun default(ownerId: DomainId) =
            DraftLetter(
                ownerId = ownerId,
                content = "",
                receiverName = "",
                images = emptyList(),
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
    }
}
