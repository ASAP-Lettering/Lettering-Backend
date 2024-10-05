package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter

interface DraftLetterManagementPort {
    fun save(draftLetter: DraftLetter): DraftLetter

    fun getDraftLetterNotNull(
        draftId: DomainId,
        ownerId: DomainId,
    ): DraftLetter

    fun update(draftLetter: DraftLetter): DraftLetter
}
