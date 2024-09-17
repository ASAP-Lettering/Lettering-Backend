package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import java.time.LocalDate

data class IndependentLetter(
    val id: DomainId = DomainId.generate(),
    val content: String,
    val images: List<String>,
    val templateType: Int,
    val senderId: DomainId? = null,
    val senderName: String,
    val receiverId: DomainId,
    val receiveDate: LocalDate,
    val isNew: Boolean = true
) {
}