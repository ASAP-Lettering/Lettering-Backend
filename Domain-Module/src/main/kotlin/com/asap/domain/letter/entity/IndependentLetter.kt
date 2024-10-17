package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import java.time.LocalDate
import java.time.LocalDateTime

data class IndependentLetter(
    val id: DomainId = DomainId.generate(),
    val content: LetterContent,
    val sender: SenderInfo,
    val receiver: ReceiverInfo,
    val receiveDate: LocalDate,
    val movedAt: LocalDateTime = LocalDateTime.now(),
    var isOpened: Boolean = false,
) {
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
            )
    }

    fun isNew(): Boolean = movedAt.isAfter(LocalDateTime.now().minusDays(1)) && isOpened.not()

    fun read() {
        isOpened = true
    }

    fun update(
        senderName: String,
        content: String,
        images: List<String>,
    ) {
        this.sender.update(senderName)
        this.content.updateContent(content)
        this.content.updateImages(images.toMutableList())
    }

    fun delete() {
        this.content.delete()
        this.sender.delete()
    }
}
