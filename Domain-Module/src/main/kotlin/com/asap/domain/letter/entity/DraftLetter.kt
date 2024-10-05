package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId

data class DraftLetter(
    val id: DomainId = DomainId.generate(),
    val content: String,
    val receiverName: String,
    val ownerId: DomainId,
    val images: List<String>,
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
}
