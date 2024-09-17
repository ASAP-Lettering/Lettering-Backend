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
        userId: DomainId
    ): SpaceLetter

    fun getSpaceLetterNotNull(id: DomainId): SpaceLetter

    fun getSpaceLetterNotNull(id: DomainId, userId: DomainId): SpaceLetter

    fun getNearbyLetter(spaceId: DomainId, userId: DomainId, letterId: DomainId): Pair<SpaceLetter?, SpaceLetter?>

    fun countLetterBySpaceId(spaceId: DomainId): Long


    fun getAllBySpaceId(spaceId: DomainId, userId: DomainId, pageRequest: PageRequest): Page<SpaceLetter>

}