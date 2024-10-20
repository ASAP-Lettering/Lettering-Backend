package com.asap.domain.letter.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.letter.enums.LetterStatus
import com.asap.domain.letter.event.SendLetterEvent
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.user.entity.User
import java.time.LocalDate
import java.time.LocalDateTime

class SendLetter(
    id: DomainId,
    val content: LetterContent,
    val senderId: DomainId,
    var receiverName: String,
    var letterCode: String?,
    var status: LetterStatus,
    val createdAt: LocalDateTime,
    var receiverId: DomainId?,
) : Aggregate<SendLetter>(id) {
    val createdDate: LocalDate = createdAt.toLocalDate()

    companion object {
        fun create(
            content: LetterContent,
            senderId: DomainId,
            receiverName: String,
            letterCode: String?,
            status: LetterStatus = LetterStatus.SENDING,
            createdAt: LocalDateTime = LocalDateTime.now(),
            receiverId: DomainId? = null,
            draftId: DomainId? = null,
        ) = SendLetter(
            id = DomainId.generate(),
            content = content,
            senderId = senderId,
            receiverName = receiverName,
            letterCode = letterCode,
            status = status,
            createdAt = createdAt,
            receiverId = receiverId,
        ).also {
            it.registerEvent(SendLetterEvent.SendLetterCreatedEvent(it, draftId?.value))
        }
    }

    fun isSameReceiver(receiver: () -> User): Boolean {
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

    fun delete() {
        this.content.delete()
        this.receiverName = ""
        this.letterCode = null
        this.receiverId = null
    }
}
