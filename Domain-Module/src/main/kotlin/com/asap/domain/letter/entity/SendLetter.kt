package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import com.asap.domain.letter.enums.LetterStatus
import com.asap.domain.letter.vo.LetterContent
import java.time.LocalDate
import java.time.LocalDateTime

data class SendLetter(
    val id: DomainId = DomainId.generate(),
    val content: LetterContent,
    val senderId: DomainId,
    val receiverName: String,
    val letterCode: String,
    val status: LetterStatus = LetterStatus.SENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val receiverId: DomainId? = null,
) {
    val createdDate: LocalDate = createdAt.toLocalDate()

    fun isSameReceiver(receiverName: () -> String): Boolean = this.receiverName == receiverName()

    fun readLetter(receiverId: DomainId): SendLetter = copy(status = LetterStatus.READ, receiverId = receiverId)

    fun receiveLetter(): SendLetter = copy(status = LetterStatus.RECEIVED, letterCode = "")
}
