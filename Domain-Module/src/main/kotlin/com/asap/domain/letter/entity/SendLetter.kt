package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import java.time.LocalDate
import java.time.LocalDateTime

data class SendLetter(
    val id: DomainId = DomainId.generate(),
    val receiverName: String,
    val content: String,
    val images: List<String>,
    val templateType: Int,
    val senderId: DomainId,
    val letterCode: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    val createdDate: LocalDate = createdAt.toLocalDate()

    fun isSameReceiver(receiverName: () -> String): Boolean {
        return this.receiverName == receiverName()
    }
}