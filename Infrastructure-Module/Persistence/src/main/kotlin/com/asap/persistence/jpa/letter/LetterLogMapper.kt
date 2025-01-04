package com.asap.persistence.jpa.letter

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.LetterLog
import com.asap.persistence.jpa.letter.entity.LetterLogEntity

object LetterLogMapper {
    fun toEntity(letterLog: LetterLog): LetterLogEntity {
        return LetterLogEntity(
            id = letterLog.id.value,
            targetLetterId = letterLog.targetLetterId.value,
            loggedAt = letterLog.loggedAt,
            logType = letterLog.logType,
            content = letterLog.content
        )
    }

    fun toDomain(letterLogEntity: LetterLogEntity): LetterLog {
        return LetterLog(
            id = DomainId(letterLogEntity.id),
            targetLetterId = DomainId(letterLogEntity.targetLetterId),
            loggedAt = letterLogEntity.loggedAt,
            logType = letterLogEntity.logType,
            content = letterLogEntity.content
        )
    }
}