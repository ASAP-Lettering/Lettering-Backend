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
    var senderId: DomainId?,
    var senderName: String?,
    var receiverName: String,
    var letterCode: String?,
    var status: LetterStatus,
    var receiverId: DomainId?,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : Aggregate<SendLetter>(id, createdAt, updatedAt) {
    val createdDate: LocalDate = createdAt.toLocalDate()

    companion object {
        fun create(
            content: LetterContent,
            senderId: DomainId,
            receiverName: String,
            letterCode: String?,
            status: LetterStatus = LetterStatus.SENDING,
            receiverId: DomainId? = null,
            draftId: DomainId? = null,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
        ) = SendLetter(
            id = DomainId.generate(),
            content = content,
            senderId = senderId,
            senderName = null,
            receiverName = receiverName,
            letterCode = letterCode,
            status = status,
            receiverId = receiverId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        ).also {
            it.registerEvent(SendLetterEvent.SendLetterCreatedEvent(it, draftId?.value))
        }

        fun createAnonymous(
            content: LetterContent,
            receiverName: String,
            letterCode: String?,
            senderName: String,
            status: LetterStatus = LetterStatus.SENDING,
            receiverId: DomainId? = null,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
        ): SendLetter =
            SendLetter(
                id = DomainId.generate(),
                content = content,
                senderId = null,
                senderName = senderName,
                receiverName = receiverName,
                letterCode = letterCode,
                status = status,
                receiverId = receiverId,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
    }

    fun isSameReceiver(receiver: User): Boolean = receiverName == receiver.username && (receiverId == null || receiverId == receiver.id)

    fun readLetter(receiverId: DomainId) {
        this.receiverId = receiverId
        this.status = LetterStatus.READ
        updateTime()
    }

    fun receiveLetter() {
        status = LetterStatus.RECEIVED
        letterCode = null
        updateTime()
    }

    fun delete() {
        this.content.delete()
        this.receiverName = ""
        this.letterCode = null
        this.receiverId = null
        updateTime()
    }

    fun configSenderId(senderId: DomainId) {
        check(this.senderId == null) {
            "SenderId is already set"
        }
        this.senderId = senderId
        updateTime()
    }
}
