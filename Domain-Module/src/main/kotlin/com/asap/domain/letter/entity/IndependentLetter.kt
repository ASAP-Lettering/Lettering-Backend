package com.asap.domain.letter.entity

import com.asap.domain.common.DomainId
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import java.time.LocalDate

data class IndependentLetter(
    val id: DomainId = DomainId.generate(),
    val content: LetterContent,
    val sender: SenderInfo,
    val receiver: ReceiverInfo,
    val receiveDate: LocalDate,
    val isNew: Boolean = true,
)
