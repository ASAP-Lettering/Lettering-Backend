package com.asap.application.letter.port.out

import com.asap.domain.letter.entity.ReceiveDraftLetter

interface ReceiveDraftLetterManagementPort {
    fun save(receiveDraftLetter: ReceiveDraftLetter): ReceiveDraftLetter
}