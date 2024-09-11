package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId

data class SendLetter(
    val id: DomainId = DomainId.generate(),
    val receiverName: String,
    val content: String,
    val images: List<String>,
    val templateType: Int,
    val senderId: DomainId,
    val letterCode: String
) {
}