package com.asap.application.letter.port.out

import com.asap.domain.letter.entity.DraftLetter

interface DraftLetterManagementPort {
    fun save(draftLetter: DraftLetter): DraftLetter
}
