package com.asap.domain.letter.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.letter.event.IndependentLetterEvent
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import java.time.LocalDate
import java.time.LocalDateTime

class IndependentLetter(
    id: DomainId,
    val content: LetterContent,
    val sender: SenderInfo,
    val receiver: ReceiverInfo,
    val receiveDate: LocalDate,
    val movedAt: LocalDateTime,
    var isOpened: Boolean,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : Aggregate<IndependentLetter>(id, createdAt, updatedAt) {
    companion object {
        fun createBySpaceLetter(
            spaceLetter: SpaceLetter,
            receiverId: DomainId,
        ): IndependentLetter =
            IndependentLetter(
                id = spaceLetter.id,
                content = spaceLetter.content,
                sender = spaceLetter.sender,
                receiver = ReceiverInfo(receiverId),
                receiveDate = spaceLetter.receiveDate,
                movedAt = LocalDateTime.now(),
                isOpened = false,
                createdAt = spaceLetter.createdAt,
                updatedAt = LocalDateTime.now(),
            )

        fun create(
            id: DomainId = DomainId.generate(),
            sender: SenderInfo,
            receiver: ReceiverInfo,
            content: LetterContent,
            receiveDate: LocalDate,
            movedAt: LocalDateTime = LocalDateTime.now(),
            isOpened: Boolean = false,
            draftId: DomainId? = null,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
        ): IndependentLetter =
            IndependentLetter(
                id = id,
                sender = sender,
                receiver = receiver,
                content = content,
                receiveDate = receiveDate,
                movedAt = movedAt,
                isOpened = isOpened,
                createdAt = createdAt,
                updatedAt = updatedAt,
            ).also {
                it.registerEvent(IndependentLetterEvent.IndependentLetterCreatedEvent(it, draftId?.value))
            }
    }

    fun isNew(): Boolean = movedAt.isAfter(LocalDateTime.now().minusDays(1)) && isOpened.not()

    fun read() {
        isOpened = true
        updateTime()
    }

    fun update(
        senderName: String,
        content: String,
        images: List<String>,
        templateType: Int,
    ) {
        this.sender.update(senderName)
        this.content.updateContent(content)
        this.content.updateImages(images.toMutableList())
        this.content.updateTemplateType(templateType)
        updateTime()
    }

    fun delete() {
        this.content.delete()
        this.sender.delete()
        updateTime()
    }

    fun getOwnerId(): DomainId = receiver.receiverId

    override fun toString(): String =
        "IndependentLetter(content=$content, sender=$sender, receiver=$receiver, receiveDate=$receiveDate, movedAt=$movedAt, isOpened=$isOpened)"
}
