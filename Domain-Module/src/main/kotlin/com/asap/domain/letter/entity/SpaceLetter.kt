package com.asap.domain.letter.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import java.time.LocalDate
import java.time.LocalDateTime

class SpaceLetter(
    id: DomainId,
    val spaceId: DomainId,
    val content: LetterContent,
    val sender: SenderInfo,
    val receiver: ReceiverInfo,
    val receiveDate: LocalDate,
    val movedAt: LocalDateTime,
) : Aggregate<SpaceLetter>(id) {
    companion object {
        fun createByIndependentLetter(
            independentLetter: IndependentLetter,
            spaceId: DomainId,
        ): SpaceLetter =
            SpaceLetter(
                id = independentLetter.id,
                spaceId = spaceId,
                content = independentLetter.content,
                sender = independentLetter.sender,
                receiver = independentLetter.receiver,
                receiveDate = independentLetter.receiveDate,
                movedAt = LocalDateTime.now(),
            )

        fun create(
            id: DomainId = DomainId.generate(),
            spaceId: DomainId,
            sender: SenderInfo,
            receiver: ReceiverInfo,
            content: LetterContent,
            receiveDate: LocalDate,
            movedAt: LocalDateTime = LocalDateTime.now(),
        ): SpaceLetter =
            SpaceLetter(
                id = id,
                spaceId = spaceId,
                sender = sender,
                receiver = receiver,
                content = content,
                receiveDate = receiveDate,
                movedAt = movedAt,
            )
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
    }
}
