package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import com.asap.domain.letter.enums.LetterStatus
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.user.entity.User
import java.time.LocalDate
import java.time.LocalDateTime

data class SendLetter(
    val id: DomainId = DomainId.generate(),
    val content: LetterContent,
    val senderId: DomainId,
    val receiverName: String,
    var letterCode: String?,
    var status: LetterStatus = LetterStatus.SENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var receiverId: DomainId? = null,
) {
    val createdDate: LocalDate = createdAt.toLocalDate()

    fun isSameReceiver(receiver: () -> User): Boolean  {
        val receiverUser = receiver()
        return receiverName == receiverUser.username && (receiverId == null || receiverId == receiverUser.id)
    }

    fun readLetter(receiverId: DomainId) {
        this.receiverId = receiverId
        this.status = LetterStatus.READ
    }

    fun receiveLetter() {
        status = LetterStatus.RECEIVED
        letterCode = null
    }
}
