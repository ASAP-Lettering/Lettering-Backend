package com.asap.application.letter.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SpaceLetter

interface SpaceLetterManagementPort {


    fun save(letter: SpaceLetter)

    fun saveByIndependentLetter(
        letter: IndependentLetter,
        spaceId: DomainId
    ): SpaceLetter

}