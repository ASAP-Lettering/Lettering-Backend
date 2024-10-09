package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter

interface IndependentLetterManagementPort {
    fun save(letter: IndependentLetter)

    fun getAllByReceiverId(receiverId: DomainId): List<IndependentLetter>

    fun getIndependentLetterByIdNotNull(id: DomainId): IndependentLetter

    fun getIndependentLetterByIdNotNull(
        id: DomainId,
        userId: DomainId,
    ): IndependentLetter

    fun countIndependentLetterByReceiverId(receiverId: DomainId): Int

    fun getNearbyLetter(
        userId: DomainId,
        letterId: DomainId,
    ): Pair<IndependentLetter?, IndependentLetter?>

    fun delete(letter: IndependentLetter)
}
