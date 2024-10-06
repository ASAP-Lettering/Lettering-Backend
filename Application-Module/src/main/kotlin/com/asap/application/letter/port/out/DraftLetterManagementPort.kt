package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter

interface DraftLetterManagementPort {
    fun save(draftLetter: DraftLetter): DraftLetter

    fun getDraftLetterNotNull(
        draftId: DomainId,
        ownerId: DomainId,
    ): DraftLetter

    fun getAllDrafts(ownerId: DomainId): List<DraftLetter>

    fun countDrafts(ownerId: DomainId): Int

    fun remove(draftLetter: DraftLetter)
}
