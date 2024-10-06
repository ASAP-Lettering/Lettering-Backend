package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter

interface IndependentLetterManagementPort {
    fun save(letter: IndependentLetter)

    fun getAllByReceiverId(receiverId: DomainId): List<IndependentLetter>

    fun getIndependentLetterByIdNotNull(id: DomainId): IndependentLetter
}
