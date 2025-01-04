package com.asap.domain.letter.entity

import com.asap.domain.common.BaseEntity
import com.asap.domain.common.DomainId
import java.time.LocalDateTime

class LetterLog(
    id: DomainId = DomainId.generate(),
    val targetLetterId: DomainId,
    val loggedAt: LocalDateTime,
    val logType: LetterLogType,
    val content: String,
) : BaseEntity(id) {
    companion object {
        fun create(
            targetLetterId: DomainId,
            logType: LetterLogType,
            content: String,
        ): LetterLog =
            LetterLog(
                targetLetterId = targetLetterId,
                loggedAt = LocalDateTime.now(),
                logType = logType,
                content = content,
            )
    }
}

enum class LetterLogType {
    SHARE,
}