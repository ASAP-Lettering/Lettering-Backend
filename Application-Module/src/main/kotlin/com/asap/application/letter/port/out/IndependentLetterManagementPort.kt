package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SpaceLetter

interface IndependentLetterManagementPort {

    fun save(letter: IndependentLetter)

    fun getAllByReceiverId(receiverId: DomainId): List<IndependentLetter>

    fun getIndependentLetterByIdNotNull(id: DomainId): IndependentLetter

    fun saveBySpaceLetter(
        letter: SpaceLetter,
        userId: DomainId
    ): IndependentLetter
}