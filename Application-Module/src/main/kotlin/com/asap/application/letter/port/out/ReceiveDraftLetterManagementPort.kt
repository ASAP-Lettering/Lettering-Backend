package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.ReceiveDraftLetter

interface ReceiveDraftLetterManagementPort {
    fun save(receiveDraftLetter: ReceiveDraftLetter): ReceiveDraftLetter

    fun getDraftLetterNotNull(draftId: DomainId, ownerId: DomainId): ReceiveDraftLetter

    fun getAllDrafts(ownerId: DomainId): List<ReceiveDraftLetter>
}