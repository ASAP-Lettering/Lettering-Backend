package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.LetterLog

interface LetterLogManagementPort {
    fun save(log: LetterLog): LetterLog

    fun findAll(): List<LetterLog>

    fun findLatestByLetterId(letterId: DomainId): LetterLog?
}