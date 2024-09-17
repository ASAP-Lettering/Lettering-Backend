package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import com.asap.domain.letter.vo.LetterContent
import java.time.LocalDate
import java.time.LocalDateTime

data class SendLetter(
    val id: DomainId = DomainId.generate(),
    val content: LetterContent,
    val senderId: DomainId,
    val receiverName: String,
    val letterCode: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    val createdDate: LocalDate = createdAt.toLocalDate()

    fun isSameReceiver(receiverName: () -> String): Boolean {
        return this.receiverName == receiverName()
    }
}