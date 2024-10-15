package com.asap.application.letter.port.out

import com.asap.common.page.Page
import com.asap.common.page.PageRequest
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SpaceLetter

interface SpaceLetterManagementPort {
    fun save(letter: SpaceLetter)

    fun saveByIndependentLetter(
        letter: IndependentLetter,
        spaceId: DomainId,
        userId: DomainId,
    ): SpaceLetter

    fun getSpaceLetterNotNull(
        id: DomainId,
        userId: DomainId,
    ): SpaceLetter

    fun getNearbyLetter(
        spaceId: DomainId,
        userId: DomainId,
        letterId: DomainId,
    ): Pair<SpaceLetter?, SpaceLetter?>

    fun countSpaceLetterBy(
        spaceId: DomainId,
        receiverId: DomainId,
    ): Long

    fun countAllSpaceLetterBy(receiverId: DomainId): Long

    fun getAllBy(
        spaceId: DomainId,
        userId: DomainId,
        pageRequest: PageRequest,
    ): Page<SpaceLetter>

    fun getAllBy(
        spaceId: DomainId,
        userId: DomainId,
    ): List<SpaceLetter>

    fun delete(letter: SpaceLetter)
}
