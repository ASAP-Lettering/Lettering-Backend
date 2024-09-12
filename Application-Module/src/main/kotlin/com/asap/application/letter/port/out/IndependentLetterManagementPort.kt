package com.asap.application.letter.port.out

import com.asap.domain.letter.entity.IndependentLetter

interface IndependentLetterManagementPort {

    fun save(letter: IndependentLetter)
}