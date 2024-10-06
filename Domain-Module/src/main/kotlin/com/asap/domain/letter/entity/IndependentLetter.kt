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
    val isOpened: Boolean = false,
) {
    companion object {
        fun createBySpaceLetter(
            spaceLetter: SpaceLetter,
            receiverId: DomainId,
        ): IndependentLetter =
            IndependentLetter(
                content = spaceLetter.content,
                sender = spaceLetter.sender,
                receiver = ReceiverInfo(receiverId),
                receiveDate = spaceLetter.receiveDate,
                movedAt = LocalDateTime.now(),
                isOpened = false,
            )
    }

    fun isNew(): Boolean = LocalDateTime.now().minusDays(1).isAfter(movedAt) || isOpened.not()
}
